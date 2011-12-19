package org.tomstools.ant.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Substitution;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.regexp.Regexp;

/**
 * 遍历文件，循环处理符合条件的数据
 * @author lotomer
 *
 */
public class ForRegexp extends Task {
    private File file;      //源文件
    private String flags;   //正则表达式标识
    private boolean byline;
    //private Union resources;
    private RegularExpression regex;
    private Substitution subs;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    private boolean preserveLastModified = false;

    private String encoding = null;

    public ForRegexp()
    {
      this.file = null;
      this.flags = "";
      this.byline = false;

      this.regex = null;
      this.subs = null;
    }

    public void setFile(File file)
    {
      this.file = file;
    }

    public void setMatch(String match)
    {
      if (this.regex != null) {
        throw new BuildException("Only one regular expression is allowed");
      }

      this.regex = new RegularExpression();
      this.regex.setPattern(match);
    }

    public void setReplace(String replace)
    {
      if (this.subs != null) {
        throw new BuildException("Only one substitution expression is allowed");
      }

      this.subs = new Substitution();
      this.subs.setExpression(replace);
    }

    public void setFlags(String flags)
    {
      this.flags = flags;
    }

    public void setByLine(boolean byline)
    {
      this.byline = byline;
    }

    public void setEncoding(String encoding)
    {
      this.encoding = encoding;
    }

//    public void addFileset(FileSet set)
//    {
//      addConfigured(set);
//    }

//    public void addConfigured(ResourceCollection rc)
//    {
//      if (!(rc.isFilesystemOnly())) {
//        throw new BuildException("only filesystem resources are supported");
//      }
//      if (this.resources == null) {
//        this.resources = new Union();
//      }
//      this.resources.add(rc);
//    }

    public RegularExpression createRegexp()
    {
      if (this.regex != null) {
        throw new BuildException("Only one regular expression is allowed.");
      }

      this.regex = new RegularExpression();
      return this.regex;
    }

    public Substitution createSubstitution()
    {
      if (this.subs != null) {
        throw new BuildException("Only one substitution expression is allowed");
      }

      this.subs = new Substitution();
      return this.subs;
    }

    public void setPreserveLastModified(boolean b)
    {
      this.preserveLastModified = b;
    }

    protected String doReplace(RegularExpression r, Substitution s, String input, int options)
    {
      String res = input;
      Regexp regexp = r.getRegexp(getProject());

      if (regexp.matches(input, options)) {
        log("Found match; substituting", 4);
        res = regexp.substitute(input, s.getExpression(getProject()), options);
      }

      return res;
    }

    protected void doReplace(File f, int options)
      throws IOException
    {
      File temp = FILE_UTILS.createTempFile("replace", ".txt", null, true, true);

      Reader r = null;
      Writer w = null;
      BufferedWriter bw = null;
      try
      {
        if (this.encoding == null) {
          r = new FileReader(f);
          w = new FileWriter(temp);
        } else {
          r = new InputStreamReader(new FileInputStream(f), this.encoding);
          w = new OutputStreamWriter(new FileOutputStream(temp), this.encoding);
        }

        BufferedReader br = new BufferedReader(r);
        bw = new BufferedWriter(w);

        boolean changes = false;

        log("Replacing pattern '" + this.regex.getPattern(getProject()) + "' with '" + this.subs.getExpression(getProject()) + "' in '" + f.getPath() + "'" + ((this.byline) ? " by line" : "") + ((this.flags.length() > 0) ? " with flags: '" + this.flags + "'" : "") + ".", 3);

        if (this.byline) {
          StringBuffer linebuf = new StringBuffer();
          String line = null;
          String res = null;

          boolean hasCR = false;
          int c;
          do {
            c = br.read();

            if (c == 13) {
              if (hasCR)
              {
                line = linebuf.toString();
                res = doReplace(this.regex, this.subs, line, options);

                if (!(res.equals(line))) {
                  changes = true;
                }

                bw.write(res);
                bw.write(13);

                linebuf = new StringBuffer();
              }
              else
              {
                hasCR = true;
              }
            } else if (c == 10)
            {
              line = linebuf.toString();
              res = doReplace(this.regex, this.subs, line, options);

              if (!(res.equals(line))) {
                changes = true;
              }

              bw.write(res);
              if (hasCR) {
                bw.write(13);
                hasCR = false;
              }
              bw.write(10);

              linebuf = new StringBuffer();
            } else {
              if ((hasCR) || (c < 0))
              {
                line = linebuf.toString();
                res = doReplace(this.regex, this.subs, line, options);

                if (!(res.equals(line))) {
                  changes = true;
                }

                bw.write(res);
                if (hasCR) {
                  bw.write(13);
                  hasCR = false;
                }

                linebuf = new StringBuffer();
              }

              if (c >= 0)
                linebuf.append((char)c);
            }
          }
          while (c >= 0);
        }
        else {
          String buf = FileUtils.safeReadFully(br);

          String res = doReplace(this.regex, this.subs, buf, options);

          if (!(res.equals(buf))) {
            changes = true;
          }

          bw.write(res);
        }

        bw.flush();

        r.close();
        r = null;
        w.close();
        w = null;

        if (changes) {
          log("File has changed; saving the updated file", 3);
          try {
            long origLastModified = f.lastModified();
            FILE_UTILS.rename(temp, f);
            if (this.preserveLastModified) {
              FILE_UTILS.setFileLastModified(f, origLastModified);
            }
            temp = null;
          } catch (IOException e) {
            throw new BuildException("Couldn't rename temporary file " + temp, e, getLocation());
          }
        }
        else {
          log("No change made", 4);
        }
      } finally {
        FileUtils.close(r);
        FileUtils.close(bw);
        FileUtils.close(w);
        if (temp != null)
          temp.delete();
      }
    }

    public void execute()
      throws BuildException
    {
      if (this.regex == null) {
        throw new BuildException("No expression to match.");
      }
      if (this.subs == null) {
        throw new BuildException("Nothing to replace expression with.");
      }

      //if ((this.file != null) && (this.resources != null)) {
      //  throw new BuildException("You cannot supply the 'file' attribute and resource collections at the same time.");
      //}

      int options = 0;

      if (this.flags.indexOf(103) != -1) {
        options |= 16;
      }

      if (this.flags.indexOf(105) != -1) {
        options |= 256;
      }

      if (this.flags.indexOf(109) != -1) {
        options |= 4096;
      }

      if (this.flags.indexOf(115) != -1) {
        options |= 65536;
      }

      if ((this.file != null) && (this.file.exists())) {
        try {
          doReplace(this.file, options);
        } catch (IOException e) {
          log("An error occurred processing file: '" + this.file.getAbsolutePath() + "': " + e.toString(), 0);
        }

      }
      else if (this.file != null)
        log("The following file is missing: '" + this.file.getAbsolutePath() + "'", 0);
//      Iterator i;
//      if (this.resources != null)
//        for (i = this.resources.iterator(); i.hasNext(); ) {
//          FileProvider fp = (FileProvider)((Resource)i.next()).as(FileProvider.class);
//
//          File f = fp.getFile();
//
//          if (f.exists()) {
//            try {
//              doReplace(f, options);
//            } catch (Exception e) {
//              log("An error occurred processing file: '" + f.getAbsolutePath() + "': " + e.toString(), 0);
//            }
//
//          }
//          else
//            log("The following file is missing: '" + f.getAbsolutePath() + "'", 0);
//        }
    }
}

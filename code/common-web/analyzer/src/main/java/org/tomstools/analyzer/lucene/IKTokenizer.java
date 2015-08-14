/**
 * 
 */
package org.tomstools.analyzer.lucene;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * @author Administrator
 *
 */
public class IKTokenizer extends Tokenizer
{
	  private IKSegmenter _IKImplement;
	  private CharTermAttribute termAtt;
	  private OffsetAttribute offsetAtt;
	  private int finalOffset;

	  public IKTokenizer(Reader in, boolean useSmart)
	  {
	    super(in);
	    this.offsetAtt = ((OffsetAttribute)addAttribute(OffsetAttribute.class));
	    this.termAtt = ((CharTermAttribute)addAttribute(CharTermAttribute.class));
	    this._IKImplement = new IKSegmenter(in, useSmart);
	  }

	  public IKTokenizer(Reader in, Configuration conf)
	  {
	    super(in);
	    this.offsetAtt = ((OffsetAttribute)addAttribute(OffsetAttribute.class));
	    this.termAtt = ((CharTermAttribute)addAttribute(CharTermAttribute.class));
	    this._IKImplement = new IKSegmenter(in, conf);
	  }
	  
	  public boolean incrementToken()
	    throws IOException
	  {
	    clearAttributes();
	    Lexeme nextLexeme = this._IKImplement.next();
	    if (nextLexeme != null)
	    {
	      this.termAtt.append(nextLexeme.getLexemeText());

	      this.termAtt.setLength(nextLexeme.getLength());

	      this.offsetAtt.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());

	      this.finalOffset = nextLexeme.getEndPosition();

	      return true;
	    }

	    return false;
	  }

//	  public void reset(Reader input)
//	    throws IOException
//	  {
//	    super.reset(input);
//	    this._IKImplement.reset(input);
//	  }

	  @Override
	public void reset() throws IOException {
		super.reset();
		this._IKImplement.reset(input);
	}

	public final void end()
	  {
	    this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
	  }
	}
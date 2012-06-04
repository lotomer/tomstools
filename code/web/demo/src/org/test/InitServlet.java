package org.test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.tomstools.common.log.Logger;
import org.tomstools.common.merge.manage.WebFileManager;
import org.tomstools.common.merge.manage.WebFileManagerFactory;

public class InitServlet extends HttpServlet {
    private final static Logger logger = Logger.getLogger(InitServlet.class);
    private static final long serialVersionUID = -1792594559209935009L;

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("start init");
        WebFileManager webFileManager = WebFileManagerFactory.getInstance().getWebFileManager();
        // 设置配置文件名
        webFileManager.addVariable("WEB_CONFIG_FILE", "config.properties");
        // 设置自定义变量
        webFileManager.addVariable("BASE_PATH", "/demo");
        //webFileManager.addVariable("LOCAL_PATH", ".");
        
        // 设置是否对文件进行压缩
        webFileManager.setNeedCompress(true);
        // 设置是否在压缩后删除原合并文件
        webFileManager.setNeedDeleteSourceFileForCompress(true);
        //执行初始化
        try {
            webFileManager.init();
        } catch (Exception e) {
            logger.fatal(e.getMessage(),e);
        }
        logger.info("init finished!");
        //DataAccessor da1 = DataAccessorFactory.getInstance().getDataAccessorBuilder().newDataAccessor();
//        DataAccessorBuilder builder = DataAccessorFactory.getInstance().getDataAccessorBuilder("oracle.jdbc.OracleDriver","jdbc:oracle:thin:@192.168.200.42:1521:ora10g", "TWTCM", "TWTCM");
//        for (int i = 0; i < 2; i++){ 
//        DataAccessor da2 = builder.newDataAccessor();
//         try {
//            //da1.open();
//            da2.open();
//            //System.out.println(da1.getConnection());
//            System.out.println(da2.getConnection());
//            da2.close();
//        } catch (DAOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        }
//         System.out.println("init...................");
//        ListHandleManager.setHandle(new ListTagHandle() {
//            
//            public String transfer(String dicType, String dicValue) {
//                return dicValue;
//            }
//            
//            public QueryResult queryDatas(String from, int pageSize, int pageNum) {
//                QueryResult result = new QueryResult();
//                result.setCount(500);
//                List<Map<String, String>> datas = new ArrayList<Map<String,String>>();
//                datas.add(createRecord("lotomer", "30"));
//                datas.add(createRecord("vaval", "26"));
//                result.setResult(datas);
//                return result;
//            }
//
//            private Map<String, String> createRecord(String name, String age) {
//                Map<String, String> result = new HashMap<String, String>(2);
//                result.put("name", name);
//                result.put("age", age);
//                return result;
//            }
//        });
    }

}

通过maven在eclipse中直接关联源码包
1、使用命令
mvn dependency:sources
2、增加参数
mvn eclipse:eclipse -DdownloadSources=true
@echo -----------------------------------------------------------------------------
@echo 生产环境打包
@echo -----------------------------------------------------------------------------
mvn clean package -Dmaven.test.skip=true -Pprod
@pause
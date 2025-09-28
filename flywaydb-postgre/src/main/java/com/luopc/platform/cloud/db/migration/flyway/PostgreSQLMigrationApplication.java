package com.luopc.platform.cloud.db.migration.flyway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * @author Robin
 */
@SpringBootApplication
public class PostgreSQLMigrationApplication {

    /**
     * 1.仅需要执行一次的，以大写“V”开头，V+版本后(版本号间的数字以“.” 或者“ _ ”分隔开，“ _ ”会自动编译成 “ . ” )+" __"+文件描述+后缀名
     * 2.需要执行多次的，以大写“R”开头，命名如R__clean.sql ，R的脚本只要改变了就会执行,R不带版本号
     * 3.V开头的比R开头的优先级要高。
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(PostgreSQLMigrationApplication.class, args);
    }
}

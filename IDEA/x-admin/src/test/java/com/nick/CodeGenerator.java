package com.nick;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

import static com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder.packageConfig;

public class CodeGenerator {
        public static void main(String[] args) {
            String url = "jdbc:mysql:///test";
            String username = "root";
            String password = "xue2004925";
            String moduleName = "sys";
            String mapperLocation = "D:\\project\\IDEA\\x-admin\\src\\main\\resources\\mapper\\" + moduleName;
            String tables = "x_user,x_menu,x_role,x_role_menu,x_user_role";
            FastAutoGenerator.create(url, username, password)
                    .globalConfig(builder -> {
                        builder.author("nick") // 设置作者
                                //.enableSwagger() // 开启 swagger 模式
                                //.fileOverride() // 覆盖已生成文件
                                .outputDir("E:\\project\\IDEA\\x-admin\\src\\main\\java"); // 指定输出目录
                    })
                    .packageConfig(builder ->
                            builder.parent("com.nick") // 设置父包名
                                    .moduleName(moduleName) // 设置父包模块名
                                    .pathInfo(Collections.singletonMap(OutputFile.xml, mapperLocation)) // 设置mapperXml生成路径
                    )
                    .strategyConfig(builder ->
                            builder.addInclude(tables) // 设置需要生成的表名
                                    .addTablePrefix("x_") // 设置过滤表前缀
                    )
                    .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                    .execute();
        }

}
package io.github.ronghuaxueleng.agent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.ronghuaxueleng.annotation.entity.Controller;
import io.github.ronghuaxueleng.utils.CommandLineUtils;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author: caoqiang
 * @create: 2020/7/25 0025 下午 13:36
 **/
public class PreMainTraceAgent {
  private static final Logger logger = Logger.getAnonymousLogger();
   private static final Gson gson = new Gson();

  /**
   * JavaAgent启动时调用的方法
   *
   * @param args            参数
   * @param instrumentation
   */
  public static void premain(String args, Instrumentation instrumentation) throws ParseException {
    //解析参数
    CommandLineUtils.parseArg(args);
    String filePath = CommandLineUtils.cmdLine.getOptionValue("f");
    logger.info("注解文件路径 : " + filePath);
    try {
      List<Controller> annoList = new ArrayList<>();
      for (String s : filePath.split(";")) {
        InputStream resourceAsStream = PreMainTraceAgent.class.getResourceAsStream(s);
        byte[] prifileContent;
        if (resourceAsStream == null) {
          prifileContent = FileUtils.readFileToByteArray(new File(s));
        } else {
          prifileContent = IOUtils.toByteArray(resourceAsStream);
        }
        String json = new String(prifileContent);
        annoList.addAll(gson.fromJson(json, new TypeToken<List<Controller>>() {
        }.getType()));
      }
      // instrumentation 中包含了项目中的全部类。每加载一次.class文件就运行一次
      instrumentation.addTransformer(new AnnotationTransformer(annoList), true);
    } catch (Exception e) {
      if ("true".equalsIgnoreCase(CommandLineUtils.cmdLine.getOptionValue("d"))) {
        e.printStackTrace();
      }
      logger.info(e.getMessage());
    }
  }
}
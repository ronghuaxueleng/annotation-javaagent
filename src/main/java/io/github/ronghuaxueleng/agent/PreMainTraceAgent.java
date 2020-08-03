package io.github.ronghuaxueleng.agent;

import com.google.gson.Gson;
import io.github.ronghuaxueleng.utils.CommandLineUtils;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.Map;

/**
 * @author: caoqiang
 * @create: 2020/7/25 0025 下午 13:36
 **/
public class PreMainTraceAgent {

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
    System.out.println("注解文件路径 : " + filePath);
    try {
      InputStream resourceAsStream = PreMainTraceAgent.class.getResourceAsStream(filePath);
      byte[] prifileContent;
      if (resourceAsStream == null) {
        prifileContent = FileUtils.readFileToByteArray(new File(filePath));
      } else {
        prifileContent = IOUtils.toByteArray(resourceAsStream);
      }
      String json = new String(prifileContent);
      Gson gson = new Gson();
      Map jsonMap = gson.fromJson(json, Map.class);
      // instrumentation 中包含了项目中的全部类。每加载一次.class文件就运行一次
      instrumentation.addTransformer(new AnnotationTransformer(jsonMap), true);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
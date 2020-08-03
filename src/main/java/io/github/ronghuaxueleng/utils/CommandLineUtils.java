package io.github.ronghuaxueleng.utils;

import org.apache.commons.cli.*;

/**
 * @author: caoqiang
 * @create: 2020/8/3 0003 下午 16:45
 **/
public class CommandLineUtils {

  public static CommandLine cmdLine;

  public static String[] translateCommandline(String toProcess) {
    if (toProcess == null || toProcess.length() == 0) {
      return new String[0];
    }

    return toProcess.split(",");
  }

  public static CommandLine parseArg(String args) throws ParseException {
    String[] strings = CommandLineUtils.translateCommandline(args);
    return parseArg(strings);
  }

  public static CommandLine parseArg(String[] args) throws ParseException {
    //定义阶段
    Options options = new Options();
    options.addOption("h", false, "使用帮助");
    options.addOption("help", false, "使用帮助");
    options.addOption("f", true, "注解文件路径");
    options.addOption("d", true, "调试时生成的class文件目录路径");

    //解析阶段
    CommandLineParser paraer = new DefaultParser();
    cmdLine = paraer.parse(options, args);
    //询问阶段
    if (cmdLine.hasOption("help") || cmdLine.hasOption("h")) {
      /*usage(); //这里作者自定义了帮助信息，其实可以使用helpFormat直接输出的*/

      HelpFormatter hf = new HelpFormatter();
      hf.setWidth(110);
      hf.printHelp("annotation-javaagent", options, true);

      System.exit(-1);
    }
    return cmdLine;
  }

  public static void main(String[] args) throws ParseException {
    String[] strings = translateCommandline("-f=Value");
    for (String string : strings) {
      System.out.println(string);
    }
    CommandLineUtils.parseArg(strings);
    String filePath = CommandLineUtils.cmdLine.getOptionValue("f");
    System.out.println(filePath);
  }

}

package io.github.ronghuaxueleng.agent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.ronghuaxueleng.annotation.entity.Controller;
import io.github.ronghuaxueleng.utils.AnnotationUtil;
import io.github.ronghuaxueleng.utils.CommandLineUtils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

/**
 * @author: caoqiang
 * @create: 2020/7/31 0031 下午 15:00
 **/
public class AnnotationTransformer implements ClassFileTransformer {

  private final Gson gson = new Gson();
  private final List<Controller> annoList;

  public AnnotationTransformer(List<Controller> annoList) {
    this.annoList = annoList;
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    for (Object anno : annoList) {
      String toJson = gson.toJson(anno);
      Controller module = gson.fromJson(toJson, new TypeToken<Controller>() {
      }.getType());
      String fullClassName = module.getFullClassName().replace(".", "/");
      // 这里指定类
      if (fullClassName.equals(className)) {
        return AnnotationUtil.get().addClassAnnotation(module, classfileBuffer, CommandLineUtils.cmdLine.getOptionValue("e"),
                "true".equalsIgnoreCase(CommandLineUtils.cmdLine.getOptionValue("d")));
      }
    }
    return new byte[0];
  }
}

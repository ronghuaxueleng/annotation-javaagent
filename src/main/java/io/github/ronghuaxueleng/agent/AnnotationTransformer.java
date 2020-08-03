package io.github.ronghuaxueleng.agent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.ronghuaxueleng.entity.BeanAnnotation;
import io.github.ronghuaxueleng.entity.BeanAnnotationAttr;
import io.github.ronghuaxueleng.entity.ClassMethod;
import io.github.ronghuaxueleng.entity.Controller;
import io.github.ronghuaxueleng.utils.AnnotationUtils;
import io.github.ronghuaxueleng.utils.CommandLineUtils;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: caoqiang
 * @create: 2020/7/31 0031 下午 15:00
 **/
public class AnnotationTransformer implements ClassFileTransformer {

  private final Map<String, Object> jsonMap;

  public AnnotationTransformer(Map<String, Object> jsonMap) {
    this.jsonMap = jsonMap;
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    Iterator<String> iterator = jsonMap.keySet().iterator();
    Gson gson = new Gson();
    while (iterator.hasNext()) {
      String next = iterator.next();
      String toJson = gson.toJson(jsonMap.get(next));
      Controller module = gson.fromJson(toJson, new TypeToken<Controller>() {
      }.getType());
      String fullClassName = module.getFullClassName().replace(".", "/");
      // 这里指定类
      if (fullClassName.equals(className)) {
        return setClassAnnotation(classfileBuffer, module);
      }
    }
    return new byte[0];
  }

  private static byte[] setClassAnnotation(byte[] classfileBuffer, Controller module) {
    // 获取一个 class 池。
    ClassPool classPool = ClassPool.getDefault();

    try {
      // 创建一个新的 class 类。classfileBuffer 就是当前class的字节码
      CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
      Set<BeanAnnotation> classAnnotations = module.getAnnotations();
      for (BeanAnnotation annotation : classAnnotations) {
        String name = annotation.getName();
        List<BeanAnnotationAttr> attrs = annotation.getAttrs();
        AnnotationUtils.get().addClassAnnotationFieldValue(ctClass, name, attrs);
      }

      List<ClassMethod> urls = module.getList();
      for (ClassMethod url : urls) {
        List<BeanAnnotation> methodAnnotations = url.getAnnotations();
        for (BeanAnnotation annotation : methodAnnotations) {
          String name = annotation.getName();
          List<BeanAnnotationAttr> attrs = annotation.getAttrs();
          AnnotationUtils.get().addMethodAnnotatioinFieldValue(ctClass, url.getMethod(), name, attrs);
        }
      }

      String savedClassDirPath = CommandLineUtils.cmdLine.getOptionValue("d");
      if (savedClassDirPath != null) {
        ctClass.writeFile(savedClassDirPath);
      }
      // 返回新的字节码
      return ctClass.toBytecode();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return new byte[0];
  }
}

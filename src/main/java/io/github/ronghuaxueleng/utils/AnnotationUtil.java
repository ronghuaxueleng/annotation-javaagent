package io.github.ronghuaxueleng.utils;

import io.github.ronghuaxueleng.annotation.entity.BeanAnnotationAttr;
import io.github.ronghuaxueleng.annotation.entity.Controller;
import io.github.ronghuaxueleng.annotation.utils.AnnotationUtils;
import javassist.CtClass;

import java.util.List;

/**
 * 注解中属性修改、查看工具
 */
public class AnnotationUtil {

  AnnotationUtils annotationUtils = new AnnotationUtils();

  private static AnnotationUtil mInstance;

  public static AnnotationUtil get() {
    if (mInstance == null) {
      synchronized (AnnotationUtil.class) {
        if (mInstance == null) {
          mInstance = new AnnotationUtil();
        }
      }
    }
    return mInstance;
  }

  public byte[] addClassAnnotation(Controller module, byte[] classfileBuffer, String savedClassDirPath, boolean debug) {
    return annotationUtils.addClassAnnotation(module, classfileBuffer, savedClassDirPath, debug);
  }

  public void addClassAnnotationFieldValue(CtClass cc, String annoName, List<BeanAnnotationAttr> attrs) {
    annotationUtils.addClassAnnotationFieldValue(cc, annoName, attrs);
  }

  public void addMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, List<BeanAnnotationAttr> attrs) {
    annotationUtils.addMethodAnnotatioinFieldValue(cc, methodName, annoName, attrs);
  }

  public void addFieldAnnotatioinFieldValue(CtClass cc, String fieldName, String annoName, List<BeanAnnotationAttr> attrs) {
    annotationUtils.addFieldAnnotatioinFieldValue(cc, fieldName, annoName, attrs);
  }
}

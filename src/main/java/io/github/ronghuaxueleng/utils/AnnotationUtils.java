package io.github.ronghuaxueleng.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.ronghuaxueleng.entity.BeanAnnotation;
import io.github.ronghuaxueleng.entity.BeanAnnotationAttr;
import io.github.ronghuaxueleng.entity.ReturnTypeField;
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 注解中属性修改、查看工具
 */
public class AnnotationUtils {

  private static AnnotationUtils mInstance;
  private Gson gson = new Gson();

  public AnnotationUtils() {
  }

  public static AnnotationUtils get() {
    if (mInstance == null) {
      synchronized (AnnotationUtils.class) {
        if (mInstance == null) {
          mInstance = new AnnotationUtils();
        }
      }
    }
    return mInstance;
  }

  /**
   * 获得类属性
   *
   * @param className 类名
   * @return
   */
  public List<ReturnTypeField> getClassFields(String className) {
    ClassPool pool = ClassPool.getDefault();
    List<ReturnTypeField> returnTypeFieldList = new ArrayList<>();
    try {
      // 创建类
      CtClass cc = pool.getCtClass(className);
      //获得所有属性
      CtField[] declaredFields = cc.getDeclaredFields();
      for (CtField declaredField : declaredFields) {
        // 获取属性的名字
        String name = declaredField.getName();
        // 将属性的首字符大写，方便构造get，set方法
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        try {
          CtMethod declaredMethod = cc.getDeclaredMethod("get" + name);
          if (declaredMethod != null) {
            ReturnTypeField returnTypeField = new ReturnTypeField();
            returnTypeField.setGenericType(declaredField.getType().getName());
            returnTypeField.setName(declaredField.getName());
            returnTypeFieldList.add(returnTypeField);
          }
        } catch (NotFoundException e) {
          System.out.println(e.getMessage());
        }
      }
    } catch (NotFoundException e) {
      System.out.println(e.getMessage());
    }
    return returnTypeFieldList;
  }

  /**
   * 修改方法注解上的属性值
   *
   * @param className  当前类名
   * @param methodName 当前方法名
   * @param annoName   方法上的注解名
   * @param fieldName  注解中的属性名
   * @param fieldValue 注解中的属性值
   * @throws NotFoundException
   */
  public void setMethodAnnotatioinFieldValue(String className, String methodName, String annoName, String fieldName, String fieldValue) throws NotFoundException {
    ClassPool classPool = ClassPool.getDefault();
    CtClass cc = classPool.get(className);
    if (cc.isFrozen()) {
      cc.defrost();
    }
    setMethodAnnotatioinFieldValue(cc, methodName, annoName, fieldName, fieldValue);
  }

  /**
   * 修改方法注解上的属性值
   *
   * @param cc         当前类
   * @param methodName 当前方法名
   * @param annoName   方法上的注解名
   * @param fieldName  注解中的属性名
   * @param fieldValue 注解中的属性值
   * @throws NotFoundException
   */
  public void setMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, String fieldName, String fieldValue) throws NotFoundException {
    if (cc.isFrozen()) {
      cc.defrost();
    }
    CtMethod ctMethod = cc.getDeclaredMethod(methodName);
    MethodInfo methodInfo = ctMethod.getMethodInfo();
    ConstPool constPool = methodInfo.getConstPool();
    AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
    Annotation annotation = attr.getAnnotation(annoName);
    if (annotation != null) {
      annotation.addMemberValue(fieldName, new StringMemberValue(fieldValue, constPool));
      attr.setAnnotation(annotation);
      methodInfo.addAttribute(attr);
    }
  }

  /**
   * 在类上添加注解
   *
   * @param cc         当前类
   * @param annoName   注解名
   * @param fieldName  字段名
   * @param fieldValue 字段值
   */
  public void addClassAnnotationFieldValue(CtClass cc, String annoName, String fieldName, Object fieldValue) {
    ClassFile classFile = cc.getClassFile();
    ConstPool constPool = classFile.getConstPool();

    // 从这里取出原本类中的注解 建议DEBUG看下attributes中的数据
    List<AttributeInfo> attributes = classFile.getAttributes();
    AnnotationsAttribute attributeInfo = (AnnotationsAttribute) attributes.get(1);
    // 添加新的注解
    Annotation annotation = new Annotation(annoName, constPool);
    attributeInfo.addAnnotation(getAnnotation(constPool, annotation, fieldName, fieldValue));
  }

  public void addClassAnnotationFieldValue(CtClass cc, String annoName, List<BeanAnnotationAttr> attrs) {
    ClassFile classFile = cc.getClassFile();
    ConstPool constPool = classFile.getConstPool();

    // 从这里取出原本类中的注解 建议DEBUG看下attributes中的数据
    List<AttributeInfo> attributes = classFile.getAttributes();
    AnnotationsAttribute attributeInfo = (AnnotationsAttribute) attributes.get(1);
    // 添加新的注解
    Annotation annotation = new Annotation(annoName, constPool);
    attributeInfo.addAnnotation(getAnnotation(constPool, annotation, attrs));
  }

  /**
   * 给方法添加注解
   *
   * @param className  类名
   * @param methodName 方法名
   * @param annoName   注解名
   * @param fieldName  字段名
   * @param fieldValue 字段值
   * @throws NotFoundException
   */
  public void addMethodAnnotatioinFieldValue(String className, String methodName, String annoName, String fieldName, String fieldValue) throws NotFoundException {
    ClassPool classPool = ClassPool.getDefault();
    CtClass cc = classPool.get(className);
    if (cc.isFrozen()) {
      cc.defrost();
    }
    addMethodAnnotatioinFieldValue(cc, methodName, annoName, fieldName, fieldValue);
  }

  /**
   * 给方法添加注解
   *
   * @param cc
   * @param methodName 方法名
   * @param annoName   注解名
   * @param fieldName  字段名
   * @param fieldValue 字段值
   * @throws NotFoundException
   */
  public void addMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, String fieldName, String fieldValue) throws NotFoundException {
    if (cc.isFrozen()) {
      cc.defrost();
    }
    CtMethod ctMethod = cc.getDeclaredMethod(methodName);
    MethodInfo methodInfo = ctMethod.getMethodInfo();
    ConstPool constPool = methodInfo.getConstPool();
    addMethodAnnotatioinFieldValue(cc, methodName, annoName, fieldName, new StringMemberValue(fieldValue, constPool));
  }

  public void addMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, List<BeanAnnotationAttr> attrs) throws NotFoundException {
    if (cc.isFrozen()) {
      cc.defrost();
    }
    CtMethod ctMethod = cc.getDeclaredMethod(methodName);
    MethodInfo methodInfo = ctMethod.getMethodInfo();
    ConstPool constPool = methodInfo.getConstPool();
    AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
    Annotation annotation = new Annotation(annoName, constPool);
    attr.addAnnotation(getAnnotation(constPool, annotation, attrs));
    methodInfo.addAttribute(attr);
  }

  public void addMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, String fieldName, Object fieldValue) throws NotFoundException {
    if (cc.isFrozen()) {
      cc.defrost();
    }
    CtMethod ctMethod = cc.getDeclaredMethod(methodName);
    MethodInfo methodInfo = ctMethod.getMethodInfo();
    ConstPool constPool = methodInfo.getConstPool();
    AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
    Annotation annotation = new Annotation(annoName, constPool);
    attr.addAnnotation(getAnnotation(constPool, annotation, fieldName, fieldValue));
    methodInfo.addAttribute(attr);
  }

  private Annotation getAnnotation(ConstPool constPool, Annotation annotation, List<BeanAnnotationAttr> attrs) {
    for (BeanAnnotationAttr attr : attrs) {
      String fieldName = attr.getAttrName();
      Object fieldValue = attr.getAttrValue();
      if (fieldValue instanceof String) {
        annotation.addMemberValue(fieldName, new StringMemberValue(fieldValue.toString(), constPool));
      } else if (fieldValue instanceof String[]) {
        String[] objects = (String[]) fieldValue;
        ArrayMemberValue memberValue = new ArrayMemberValue(constPool);
        StringMemberValue[] stringMemberValues = new StringMemberValue[objects.length];
        for (int i = 0; i < objects.length; i++) {
          stringMemberValues[i] = new StringMemberValue(objects[i], constPool);
        }
        memberValue.setValue(stringMemberValues);
        annotation.addMemberValue(fieldName, memberValue);
      } else if (fieldValue instanceof List) {
        List<Object> objects = (List<Object>) fieldValue;
        if (objects.size() > 0) {
          ArrayMemberValue memberValue = new ArrayMemberValue(constPool);
          if (objects.get(0) instanceof String) {
            List<String> stringObjects = (List<String>) fieldValue;
            StringMemberValue[] stringMemberValues = new StringMemberValue[stringObjects.size()];
            for (int i = 0; i < stringObjects.size(); i++) {
              stringMemberValues[i] = new StringMemberValue(stringObjects.get(i), constPool);
            }
            memberValue.setValue(stringMemberValues);
          } else if (objects.get(0) instanceof Map) {
            String toJson = gson.toJson(fieldValue);
            List<BeanAnnotation> subAnnotations = gson.fromJson(toJson, new TypeToken<List<BeanAnnotation>>() {
            }.getType());
            List<AnnotationMemberValue> annotationMemberValues = new ArrayList<>();
            for (int i = 0; i < subAnnotations.size(); i++) {
              BeanAnnotation subAnnotation = subAnnotations.get(i);
              String subAnnotationName = subAnnotation.getName();
              List<BeanAnnotationAttr> subAnnotationAttrs = subAnnotation.getAttrs();
              Annotation anno = getAnnotation(constPool, new Annotation(subAnnotationName, constPool), subAnnotationAttrs);
              AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(constPool);
              annotationMemberValue.setValue(anno);
              annotationMemberValues.add(annotationMemberValue);
            }
            memberValue.setValue(annotationMemberValues.toArray(new MemberValue[0]));
          }
          annotation.addMemberValue(fieldName, memberValue);
        }
      }
    }
    return annotation;
  }

  private Annotation getAnnotation(ConstPool constPool, Annotation annotation, String fieldName, Object fieldValue) {
    if (fieldValue instanceof String) {
      annotation.addMemberValue(fieldName, new StringMemberValue(fieldValue.toString(), constPool));
    } else if (fieldValue instanceof String[]) {
      String[] objects = (String[]) fieldValue;
      ArrayMemberValue memberValue = new ArrayMemberValue(constPool);
      StringMemberValue[] stringMemberValues = new StringMemberValue[objects.length];
      for (int i = 0; i < objects.length; i++) {
        stringMemberValues[i] = new StringMemberValue(objects[i], constPool);
      }
      memberValue.setValue(stringMemberValues);
      annotation.addMemberValue(fieldName, memberValue);
    } else if (fieldValue instanceof List) {
      List<String> objects = (List<String>) fieldValue;
      ArrayMemberValue memberValue = new ArrayMemberValue(constPool);
      StringMemberValue[] stringMemberValues = new StringMemberValue[objects.size()];
      for (int i = 0; i < objects.size(); i++) {
        stringMemberValues[i] = new StringMemberValue(objects.get(i), constPool);
      }
      memberValue.setValue(stringMemberValues);
      annotation.addMemberValue(fieldName, memberValue);
    }
    return annotation;
  }

  /**
   * 给方法添加注解
   *
   * @param cc
   * @param methodName 方法名
   * @param annoName   注解名
   * @param fieldName  字段名
   * @param fieldValue 字段值
   * @throws NotFoundException
   */
  public void addMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, String fieldName, MemberValue fieldValue) throws NotFoundException {
    if (cc.isFrozen()) {
      cc.defrost();
    }
    CtMethod ctMethod = cc.getDeclaredMethod(methodName);
    MethodInfo methodInfo = ctMethod.getMethodInfo();
    ConstPool constPool = methodInfo.getConstPool();
    AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
    Annotation annot = new Annotation(annoName, constPool);
    annot.addMemberValue(fieldName, fieldValue);
    attr.addAnnotation(annot);
    methodInfo.addAttribute(attr);
  }

  /**
   * 获取方法注解中的属性值
   *
   * @param className  当前类名
   * @param methodName 当前方法名
   * @param annoName   方法上的注解名
   * @param fieldName  注解中的属性名
   * @return
   * @throws NotFoundException
   */
  public String getMethodAnnotatioinFieldValue(String className, String methodName, String annoName, String fieldName) throws NotFoundException {
    ClassPool classPool = ClassPool.getDefault();
    CtClass cc = classPool.get(className);
    return getMethodAnnotatioinFieldValue(cc, methodName, annoName, fieldName);
  }

  /**
   * 获取方法注解中的属性值
   *
   * @param cc
   * @param methodName 当前方法名
   * @param annoName   方法上的注解名
   * @param fieldName  注解中的属性名
   * @return
   * @throws NotFoundException
   */
  public String getMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, String fieldName) throws NotFoundException {
    if (cc.isFrozen()) {
      cc.defrost();
    }
    CtMethod ctMethod = cc.getDeclaredMethod(methodName);
    MethodInfo methodInfo = ctMethod.getMethodInfo();
    AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
    String value = "";
    if (attr != null) {
      Annotation an = attr.getAnnotation(annoName);
      if (an != null) {
        value = ((StringMemberValue) an.getMemberValue(fieldName)).getValue();
      }
    }
    return value;
  }

  /***
   * 使用javaassist的反射方法获取方法的参数名
   *
   * @param handlerMap
   *          clazz：类的class对象
   *          method ：方法的Method对象
   *
   * @return
   */
  public Map<String, Object> getMethodParams(Map<String, Object> handlerMap) {
    Class<?> clazz = (Class) handlerMap.get("clazz");
    Method method = (Method) handlerMap.get("method");
    Map<String, Object> params = new LinkedHashMap<>();

    try {
      ClassPool pool = ClassPool.getDefault();
      ClassClassPath classPath = new ClassClassPath(this.getClass());
      pool.insertClassPath(classPath);
      CtClass cc = pool.get(clazz.getName());
      CtMethod cm = cc.getDeclaredMethod(method.getName());
      MethodInfo methodInfo = cm.getMethodInfo();
      CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
      LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
      if (attr == null) {
        throw new IllegalArgumentException("获取参数错误");
      }
      int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
      String[] paramNames = new String[cm.getParameterTypes().length];
      Class<?>[] parameterTypes = method.getParameterTypes();
      for (int i = 0; i < paramNames.length; i++) {
        paramNames[i] = attr.variableName(i + pos);
        params.put(paramNames[i], parameterTypes[i]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return params;
  }

  /***
   *  通过类名 方法名获取方法中的参数名
   *
   * @param classname 类的完整路径
   * @param methodname  方法名
   * @return
   */
  public List<Map<String, Object>> getMethodParams(String classname, String methodname, List<Class> includeAnnotations) {
    List<Map<String, Object>> list = new ArrayList<>();
    try {
      ClassPool pool = ClassPool.getDefault();
      CtClass cc = pool.get(classname);
      if (cc.isFrozen()) {
        cc.defrost();
      }
      CtMethod cm = cc.getDeclaredMethod(methodname);
      MethodInfo methodInfo = cm.getMethodInfo();
      CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
      CtClass[] parameterTypes = cm.getParameterTypes();
      Object[][] parameterAnnotations = cm.getParameterAnnotations();
      String[] paramNames = new String[parameterTypes.length];
      LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
      if (attr != null) {
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
          String s = attr.variableName(i + pos);
          if ("this".equals(s) || "request".equals(s)) {
            break;
          }
          Map<String, Object> map = new HashMap<>();
          map.put("name", s);
          map.put("type", parameterTypes[i].getName());
          for (Object o : parameterAnnotations[i]) {
            includeAnnotations.forEach(includeAnnotation -> {
              if (includeAnnotation.isInstance(o)) {
                map.put("annotation", o);
                map.put("annotationType", includeAnnotation.getName());
              }
            });
          }
          list.add(map);
        }
      }
    } catch (Exception e) {
      System.out.println("getMethodVariableName fail " + e);
    }
    return list;
  }
}

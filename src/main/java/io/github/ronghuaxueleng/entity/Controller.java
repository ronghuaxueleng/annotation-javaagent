package io.github.ronghuaxueleng.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: caoqiang
 * @create: 2020/7/26 0026 下午 19:31
 **/
@Data
public class Controller {
  /**
   * 类名全路径
   */
  private String fullClassName;
  /**
   * 类名
   */
  private String controllerName;
  /**
   * 作用域
   */
  private String scope = "controller";
  /**
   * 方法列表
   */
  private List<ClassMethod> list = new ArrayList<>();
  /**
   * 注解列表
   */
  private Set<BeanAnnotation> annotations = new HashSet<>();
}
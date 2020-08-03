package io.github.ronghuaxueleng.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: caoqiang
 * @create: 2020/7/31 0031 下午 17:52
 **/
@Data
@NoArgsConstructor
public class ClassMethod {
  /**
   * 请求类型
   */
  private String type;
  /**
   * 处理后的url
   */
  private String url;
  /**
   * code
   */
  private String code;
  /**
   * 所在类方法名
   */
  private String method;
  /**
   * url全路径
   */
  private String fullUrl;
  /**
   * 作用域
   */
  private String scope = "methond";
  /**
   * 参数列表
   */
  private List<String> params = new ArrayList<>();
  /**
   * 方法中文名
   */
  private String methondName;
  /**
   * 所在路径名
   */
  private String fullClassName;
  /**
   * 参数字符串
   */
  private String paramsString;
  /**
   * 是否isPathVariable
   */
  private boolean isPathVariable;
  /**
   * request参数列表
   */
  private List<String> requestParamParams = new ArrayList<>();
  /**
   * pathVariable参数列表
   */
  private List<String> pathVariableParams = new ArrayList<>();
  /**
   * 注解列表
   */
  private List<BeanAnnotation> annotations = new ArrayList<>();
  /**
   * 返回值
   */
  private ReturnType methodReturnType;
  /**
   * 参数列表
   */
  private List<MethodParam> methodParams = new ArrayList<>();
}

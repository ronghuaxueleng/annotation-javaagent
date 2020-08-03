package io.github.ronghuaxueleng.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: caoqiang
 * @create: 2020/8/1 0001 下午 14:32
 **/
@Data
@NoArgsConstructor
public class MethodParam {
  /**
   * 参数名称
   */
  private String name;
  /**
   * 中文名
   */
  private String cnName;
  /**
   * 参数类型
   */
  private String type;

  /**
   * 注解类型
   */
  private String annoType;
  /**
   * 参数字段
   */
  private List<ReturnTypeField> fields;
}

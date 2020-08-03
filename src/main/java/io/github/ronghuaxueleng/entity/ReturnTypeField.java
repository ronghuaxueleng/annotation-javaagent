package io.github.ronghuaxueleng.entity;

import lombok.Data;

/**
 * 返回值字段
 *
 * @author: caoqiang
 * @create: 2020/7/27 0027 下午 20:57
 **/
@Data
public class ReturnTypeField {

  /**
   * 字段名
   */
  private String name;

  /**
   * 类型
   */
  private String genericType;
}

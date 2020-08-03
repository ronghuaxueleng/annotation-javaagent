package io.github.ronghuaxueleng.entity;

import lombok.Data;

import java.util.List;

/**
 * @author: caoqiang
 * @create: 2020/7/27 0027 下午 21:00
 **/
@Data
public class ReturnType {

  /**
   * 返回类型类名
   */
  private String returnTypeName;

  /**
   * 字段列表
   */
  private List<ReturnTypeField> returnTypeFields;

}

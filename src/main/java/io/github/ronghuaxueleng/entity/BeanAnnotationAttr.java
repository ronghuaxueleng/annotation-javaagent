package io.github.ronghuaxueleng.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: caoqiang
 * @create: 2020/8/2 0002 下午 16:14
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanAnnotationAttr {
  /**
   * 属性名
   */
  private String attrName;

  /**
   * 属性值
   */
  private Object attrValue;
}

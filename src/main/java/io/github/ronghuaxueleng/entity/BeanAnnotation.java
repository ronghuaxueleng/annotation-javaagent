package io.github.ronghuaxueleng.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 注解
 *
 * @author: caoqiang
 * @create: 2020/7/31 0031 下午 16:13
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanAnnotation {
  /**
   * 注解名称
   */
  private String name;

  private List<BeanAnnotationAttr> attrs = new ArrayList<>();
}

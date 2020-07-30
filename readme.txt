1.由于springboot中@ComponentScan是搜索注册注解类所在以及下级目录的spring bean，因此作为外部组件
  禁止使用@component，@service等注册bean，因为注册了也不会生效。可以使用@Configuration类下面
  的@bean方法，或@import来注册bean
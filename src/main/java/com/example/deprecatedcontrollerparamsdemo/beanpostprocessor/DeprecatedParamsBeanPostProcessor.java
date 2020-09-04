package com.example.deprecatedcontrollerparamsdemo.beanpostprocessor;

import com.example.deprecatedcontrollerparamsdemo.controller.InterceptorController;
import com.example.deprecatedcontrollerparamsdemo.interceptor.SubstituteParams;
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

//@Component
public class DeprecatedParamsBeanPostProcessor implements BeanPostProcessor {

    @Override
    @SneakyThrows
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!InterceptorController.class.isAssignableFrom(bean.getClass())) {
            return bean;
        }
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(bean.getClass()));
        CtClass parentCtClass = classPool.get(bean.getClass().getName());
        CtClass ctClass = classPool.makeClass(bean.getClass().getName() + "Extension", parentCtClass);

        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            SubstituteParams declaredAnnotation = method.getDeclaredAnnotation(SubstituteParams.class);
            if (declaredAnnotation != null) {
                //CtNewMethod.
                //CtMethod declaredMethod = parentCtClass.getDeclaredMethod(method.getName());

                /*CtMethod copy = CtNewMethod.copy(declaredMethod, ctClass, null);
                ctClass.addMethod(copy);*/

                //CtMethod delegator = CtNewMethod.delegator(declaredMethod, ctClass);

                CtMethod delegator = CtNewMethod.make("public ResponseBodyDto someActualMethod(@RequestParam String actualParam, @RequestBody RequestBodyDto body) { return $proceed(actualParam, body); }", ctClass, "super", "someActualMethod");



                /*for(Object attribute: declaredMethod.getMethodInfo().getAttributes()) {
                    delegator.getMethodInfo().addAttribute((AttributeInfo)attribute);
                }*/
                //delegator.insertBefore("System.out.println(\"THIS IS DELEGATOOOOOR!\");");

                /*AttributeInfo parameterAttributeInfo = declaredMethod.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);
                ConstPool parameterConstPool = parameterAttributeInfo.getConstPool();
                ParameterAnnotationsAttribute parameterAttribute = ((ParameterAnnotationsAttribute) parameterAttributeInfo);
                Annotation[][] paramArrays = parameterAttribute.getAnnotations();
                Annotation[] addAnno = paramArrays[0];
                //-- Edit the annotation adding values
                addAnno[0].addMemberValue("value", new StringMemberValue("This is the value of the annotation", parameterConstPool));
                addAnno[0].addMemberValue("required", new BooleanMemberValue(Boolean.TRUE, parameterConstPool));
                paramArrays[0] = addAnno;
                parameterAttribute.setAnnotations(paramArrays);*/


                /*ConstPool constPool = ctClass.getClassFile().getConstPool();
                ParameterAnnotationsAttribute attr = new ParameterAnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation requestParam = new Annotation(RequestParam.class.getTypeName(), constPool);
                requestParam.addMemberValue("name", new StringMemberValue("actualParam", constPool));
                Annotation requestBody = new Annotation(RequestBody.class.getTypeName(), constPool);
                Annotation[][] annotations = {{requestParam}, {requestBody}};
                attr.setAnnotations(annotations);
                delegator.getMethodInfo().addAttribute(attr);*/

                ctClass.addMethod(delegator);
            }
        }

        Class<?> clazz = ctClass.toClass();

        Constructor<?> constructor = clazz.getConstructors()[0];
        Object o = constructor.newInstance();


        /*ClassFile cf = new ClassFile(
                false, , bean.getClass().getCanonicalName());*/

        /*cf.addMethod(new MethodInfo(cf.getConstPool()));

        FieldInfo f = new FieldInfo(cf.getConstPool(), "id", "I");
        f.setAccessFlags(AccessFlag.PUBLIC);
        cf.addField(f);


        Field[] fields = classPool.makeClass(cf).toClass().getFields();*/

        //assertEquals(fields[0].getName(), "id");

        return o;
    }
}

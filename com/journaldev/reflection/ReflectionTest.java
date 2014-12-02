package com.journaldev.reflection;

import java.util.*;
import java.lang.reflect.*;

public class ReflectionTest{

	public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, 
							InstantiationException, InvocationTargetException {

	// Get Class using reflection
	Class<?> concreteClass = ConcreteClass.class;
	System.out.println(concreteClass.getCanonicalName());

	concreteClass = new ConcreteClass(5).getClass();
	System.out.println(concreteClass.getCanonicalName());

	try {
    	// below method is used most of the times in frameworks like JUnit
    	//Spring dependency injection, Tomcat web container
    	//Eclipse auto completion of method names, hibernate, Struts2 etc.
    	//because ConcreteClass is not available at compile time
    		concreteClass = Class.forName("com.journaldev.reflection.ConcreteClass");
	} catch (ClassNotFoundException e) {
    		e.printStackTrace();
	}
	System.out.println(concreteClass.getCanonicalName()); // prints com.journaldev.reflection.ConcreteClass
 
	//for primitive types, wrapper classes and arrays
	Class<?> booleanClass = boolean.class;
	System.out.println(booleanClass.getCanonicalName()); // prints boolean
 
	Class<?> cDouble = Double.TYPE;
	System.out.println(cDouble.getCanonicalName()); // prints double
 
	try {
		Class<?> cDoubleArray = Class.forName("[D");
		System.out.println(cDoubleArray.getCanonicalName()); //prints double[]
	} catch (ClassNotFoundException e) {
    		e.printStackTrace();
	}
 
	Class<?> twoDStringArray = String[][].class;
	System.out.println(twoDStringArray.getCanonicalName()); // prints java.lang.String[][]

	System.out.println("---------------------------");

	try {
		Class<?> superClass = Class.forName("com.journaldev.reflection.ConcreteClass").getSuperclass();
		System.out.println(superClass); // prints "class com.journaldev.reflection.BaseClass"
	} catch (ClassNotFoundException e) {
    		e.printStackTrace();
	}	
	System.out.println(Object.class.getSuperclass()); // prints "null"
	System.out.println(String[][].class.getSuperclass());// prints "class java.lang.Object"

	System.out.println("---------------------------");

	Class<?>[] classes = concreteClass.getClasses();
	//[class com.journaldev.reflection.ConcreteClass$ConcreteClassPublicClass, 
	//class com.journaldev.reflection.ConcreteClass$ConcreteClassPublicEnum, 
	//interface com.journaldev.reflection.ConcreteClass$ConcreteClassPublicInterface,
	//class com.journaldev.reflection.BaseClass$BaseClassInnerClass, 
	//class com.journaldev.reflection.BaseClass$BaseClassMemberEnum]
	System.out.println(Arrays.toString(classes));

	System.out.println("---------------------------");
	//getting all of the classes, interfaces, and enums that are explicitly declared in ConcreteClass
	Class<?>[] explicitClasses = Class.forName("com.journaldev.reflection.ConcreteClass").getDeclaredClasses();
	//prints [class com.journaldev.reflection.ConcreteClass$ConcreteClassDefaultClass, 
	//class com.journaldev.reflection.ConcreteClass$ConcreteClassDefaultEnum, 
	//class com.journaldev.reflection.ConcreteClass$ConcreteClassPrivateClass, 
	//class com.journaldev.reflection.ConcreteClass$ConcreteClassProtectedClass, 
	//class com.journaldev.reflection.ConcreteClass$ConcreteClassPublicClass, 
	//class com.journaldev.reflection.ConcreteClass$ConcreteClassPublicEnum, 
	//interface com.journaldev.reflection.ConcreteClass$ConcreteClassPublicInterface]
	System.out.println(Arrays.toString(explicitClasses));

	System.out.println("---------------------------");
	Class<?> innerClass = Class.forName("com.journaldev.reflection.ConcreteClass$ConcreteClassDefaultClass");
	//prints com.journaldev.reflection.ConcreteClass
	System.out.println(innerClass.getDeclaringClass().getCanonicalName());
	System.out.println(innerClass.getEnclosingClass().getCanonicalName());

	System.out.println("---------------------------");
	//prints "com.journaldev.reflection"
	System.out.println(Class.forName("com.journaldev.reflection.BaseInterface").getPackage().getName());

	System.out.println("---------------------------");
	System.out.println(Modifier.toString(concreteClass.getModifiers())); //prints "public"
	//prints "public abstract interface"
	System.out.println(Modifier.toString(Class.forName("com.journaldev.reflection.BaseInterface").getModifiers())); 

	System.out.println("---------------------------");
	//Get Type parameters (generics)
	TypeVariable<?>[] typeParameters = Class.forName("java.util.HashMap").getTypeParameters();
	for(TypeVariable<?> t : typeParameters)
		System.out.print(t.getName()+",");

	System.out.println("---------------------------");
	Type[] interfaces = Class.forName("java.util.HashMap").getGenericInterfaces();
	//prints "1"
	System.out.println(Arrays.toString(interfaces));
	//prints "[interface java.util.Map, interface java.lang.Cloneable, interface java.io.Serializable]"
	System.out.println(Arrays.toString(Class.forName("java.util.HashMap").getInterfaces()));

	System.out.println("---------------------------");
	Method[] publicMethods = Class.forName("com.journaldev.reflection.ConcreteClass").getMethods();
	//prints public methods of ConcreteClass, BaseClass, Object
	System.out.println(Arrays.toString(publicMethods));

	System.out.println("---------------------------");
	//Get All public constructors
	Constructor<?>[] publicConstructors = Class.forName("com.journaldev.reflection.ConcreteClass").getConstructors();
	//prints public constructors of ConcreteClass
	System.out.println(Arrays.toString(publicConstructors));

	System.out.println("---------------------------");
	//Get All public fields
	Field[] publicFields = Class.forName("com.journaldev.reflection.ConcreteClass").getFields();
	//prints public fields of ConcreteClass, it's superclass and super interfaces
	System.out.println(Arrays.toString(publicFields));

	System.out.println("---------------------------");
	java.lang.annotation.Annotation[] annotations = Class.forName("com.journaldev.reflection.ConcreteClass").getAnnotations();
	//prints [@java.lang.Deprecated()]
	System.out.println(Arrays.toString(annotations));

	System.out.println("---------------------------");
	Field field = Class.forName("com.journaldev.reflection.ConcreteClass").getField("interfaceInt");

	System.out.println("---------------------------");
	try {
    		Field field1 = Class.forName("com.journaldev.reflection.ConcreteClass").getField("interfaceInt");
    		Class<?> fieldClass = field1.getDeclaringClass();
    		System.out.println(fieldClass.getCanonicalName()); //prints com.journaldev.reflection.BaseInterface
	} catch (NoSuchFieldException e) {
    		e.printStackTrace();
	} catch (SecurityException e) {
		e.printStackTrace();
	}

	System.out.println("---------------------------");
	Field field2 = Class.forName("com.journaldev.reflection.ConcreteClass").getField("publicInt");
	Class<?> fieldType = field2.getType();
	System.out.println(fieldType.getCanonicalName()); //prints int
    
	System.out.println("---------------------------");
	Field field3 = Class.forName("com.journaldev.reflection.ConcreteClass").getField("publicInt");
	ConcreteClass obj = new ConcreteClass(5);
	System.out.println(field3.get(obj)); //prints 5
	field3.setInt(obj, 10); //setting field value to 10 in object
	System.out.println(field3.get(obj)); //prints 10

	System.out.println("---------------------------");
	Field privateField = Class.forName("com.journaldev.reflection.ConcreteClass").getDeclaredField("privateString");
	//turning off access check with below method call
	privateField.setAccessible(true);
	ConcreteClass objTest = new ConcreteClass(1);
	System.out.println(privateField.get(objTest)); // prints "private string"
	privateField.set(objTest, "private string updated");
	System.out.println(privateField.get(objTest)); //prints "private string updated"

	System.out.println("---------------------------");
	Method method = Class.forName("java.util.HashMap").getMethod("put", Object.class, Object.class);
	//get method parameter types, prints "[class java.lang.Object, class java.lang.Object]"
	System.out.println(Arrays.toString(method.getParameterTypes()));
	//get method return type, return "class java.lang.Object", class reference for void
	System.out.println(method.getReturnType());
	//get method modifiers
	System.out.println(Modifier.toString(method.getModifiers())); //prints "public"

	System.out.println("---------------------------");
	Method method2 = Class.forName("java.util.HashMap").getMethod("put", Object.class, Object.class);
	Map<String, String> hm = new HashMap<String, String>();
	method2.invoke(hm, "key", "value");
	System.out.println(hm); // prints {key=value}

	System.out.println("---------------------------");
	//invoking private method
	Method method3 = Class.forName("com.journaldev.reflection.BaseClass").getDeclaredMethod("method3", null);
	method3.setAccessible(true);
	method3.invoke(null, null); //prints "Method3"

	System.out.println("---------------------------");
	Constructor<?> constructor = Class.forName("com.journaldev.reflection.ConcreteClass").getConstructor(int.class);
	//getting constructor parameters
	System.out.println(Arrays.toString(constructor.getParameterTypes())); // prints "[int]"
 
	Constructor<?> hashMapConstructor = Class.forName("java.util.HashMap").getConstructor(null);
	System.out.println(Arrays.toString(hashMapConstructor.getParameterTypes())); // prints "[]"

	System.out.println("---------------------------"); 
	Object myObj = constructor.newInstance(10);
	Method myObjMethod = myObj.getClass().getMethod("method1", null);
	myObjMethod.invoke(myObj, null); //prints "Method1 impl."

	HashMap<String,String> myMap = (HashMap<String,String>) hashMapConstructor.newInstance(null);

	System.out.println("---------------------------");
	}
}

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.WeakHashMap;

public class ReferenceTest {
	private static ReferenceQueue<Grocery> rqs = new ReferenceQueue<Grocery>();
	private static ReferenceQueue<Grocery> rqw = new ReferenceQueue<Grocery>();
	private static ReferenceQueue<Grocery> rqp = new ReferenceQueue<Grocery>();

	public static void checkQueue(String type, ReferenceQueue<Grocery> rq) {
		Reference<? extends Grocery> inq = rq.poll();
		if (inq != null) {
			System.out.println(type + " -> " + inq.get() + "|" + inq);
		}
	}

	public static void main(String[] args) throws Exception {
		final int size = 10;

		Set<SoftReference<Grocery>> sa = new HashSet<SoftReference<Grocery>>();
		for (int i = 0; i < size; i++) {
			SoftReference<Grocery> ref = new SoftReference<Grocery>(
					new Grocery("Soft" + i), rqs);
			System.out.println("Just created " + ref.get());
			sa.add(ref);
			checkQueue("soft", rqs);
		}
		System.out.println("-- call system gc --");
		System.gc();
		checkQueue("soft", rqs);

		Set<WeakReference<Grocery>> wa = new HashSet<WeakReference<Grocery>>();
		for (int i = 0; i < size; i++) {
			WeakReference<Grocery> ref = new WeakReference<Grocery>(
					new Grocery("Weak" + i), rqw);
			System.out.println("Just created " + ref.get());
			wa.add(ref);
			checkQueue("weak", rqw);
		}
		System.out.println("-- call system gc --");
		System.gc();
		checkQueue("weak", rqw);

		Set<PhantomReference<Grocery>> pa = new HashSet<PhantomReference<Grocery>>();
		for (int i = 0; i < size; i++) {
			PhantomReference<Grocery> ref = new PhantomReference<Grocery>(
					new Grocery("Phantom" + i), rqp);
			System.out.println("Just created " + ref.get());
			pa.add(ref);
			checkQueue("phantom", rqp);
		}
		System.out.println("-- call system gc --");
		System.gc();
		checkQueue("phantom", rqp);

		System.out.println("====================================");

		int array_size = 20;
		Key[] keys = new Key[array_size];
		WeakHashMap<Key, Value> map = new WeakHashMap<Key, Value>();
		for (int i = 0; i < array_size; i++) {
			Key k = new Key(Integer.toString(i));
			Value v = new Value(Integer.toString(i));
			if (i % 3 == 0) {
				keys[i] = k;
			}
			map.put(k, v);
		}
		System.gc();

		System.out.println("====================================");

		/*
		 * 只有当内存不够的时候，才回收这类内存，因此在内存足够的时候，它们通常不被回收 无论是否发送GC,执行结果都是:
		 * java.lang.Object@f9f9d8 null java.lang.Object@f9f9d8 null
		 */
		Object obj = new Object();
		ReferenceQueue<Object> refQueue = new ReferenceQueue<Object>();
		SoftReference<Object> softRef = new SoftReference<Object>(obj, refQueue);
		System.out.println(softRef.get());
		System.out.println(refQueue.poll());
		// 清除强引用,触发GC
		obj = null;
		System.gc();
		System.out.println(softRef.get());
		Thread.sleep(200);
		System.out.println(refQueue.poll());
		System.out.println("----------");
		/*
		 * 弱引用: 当发生GC的时候,Weak引用对象总是会内回收回收。因此Weak引用对象会更容易、更快被GC回收。
		 * Weak引用对象常常用于Map数据结构中，引用占用内存空间较大的对象 如果不发生垃圾回收： java.lang.Object@f9f9d8
		 * null java.lang.Object@f9f9d8 null 如果发生垃圾回收: java.lang.Object@f9f9d8
		 * null null java.lang.ref.WeakReference@422ede
		 */
		obj = new Object();
		refQueue = new ReferenceQueue<Object>();
		WeakReference<Object> weakRef = new WeakReference<Object>(obj, refQueue);
		System.out.println(weakRef.get()); // java.lang.Object@f9f9d8
		System.out.println(refQueue.poll());// null
		// 清除强引用,触发GC
		obj = null;
		System.gc();
		System.out.println(weakRef.get());
		// 这里特别注意:poll是非阻塞的,remove是阻塞的.
		// JVM将弱引用放入引用队列需要一定的时间,所以这里先睡眠一会儿
		//System.out.println(refQueue.poll());// 这里有可能是null
		Thread.sleep(200);
		System.out.println(refQueue.poll());
		//System.out.println(refQueue.remove());
		//System.out.println(refQueue.poll());// 这里一定是null,因为已经从队列中移除
		System.out.println("----------");
		/*
		 * GC一但发现了虚引用对象，将会将PhantomReference对象插入ReferenceQueue队列.
		 * 而此时PhantomReference所指向的对象并没有被GC回收，而是要等到ReferenceQueue被你真正的处理后才会被回收.
		 * 虚引用在实现一个对象被回收之前必须做清理操作是很有用的,比finalize()方法更灵活 不发生GC执行结果是: null null
		 * null null 发生GC执行结果是: null null null
		 * java.lang.ref.PhantomReference@87816d
		 */
		obj = new Object();
		refQueue = new ReferenceQueue<Object>();
		PhantomReference<Object> phantom = new PhantomReference<Object>(obj,
				refQueue);
		System.out.println(phantom.get());
		System.out.println(refQueue.poll());
		obj = null;
		System.gc();
		// 调用phanRef.get()不管在什么情况下会一直返回null
		System.out.println(phantom.get());
		// 当GC发现了虚引用，GC会将phanRef插入进我们之前创建时传入的refQueue队列
		// 注意，此时phanRef所引用的obj对象，并没有被GC回收，在我们显式地调用refQueue.poll返回phanRef之后
		// 当GC第二次发现虚引用，而此时JVM将phanRef插入到refQueue会插入失败，此时GC才会对obj进行回收
		Thread.sleep(200);
		System.out.println(refQueue.poll());

	}
}

class Grocery {
	private static final int SIZE = 1000;
	private double[] d = new double[SIZE];
	private String id;

	public Grocery(String id) {
		this.id = id;
	}

	public String toString() {
		return id;
	}

	public void finalize() {
		System.out.println("Finalizing " + id);
	}
}

class Element {
	private String ident;

	public Element(String id) {
		ident = id;
	}

	public String toString() {
		return ident;
	}

	public int hashCode() {
		return ident.hashCode();
	}

	public boolean equals(Object obj) {
		return obj instanceof Element && ident.equals(((Element) obj).ident);
	}

	protected void finalize() {
		System.out.println("Finalizing " + getClass().getSimpleName() + " "
				+ ident);
	}
}

class Key extends Element {
	public Key(String id) {
		super(id);
	}
}

class Value extends Element {
	public Value(String id) {
		super(id);
	}
}

class Employee {
	private String id;// 雇员的标识号码
	private String name;// 雇员姓名
	private String department;// 该雇员所在部门
	private String Phone;// 该雇员联系电话
	private int salary;// 该雇员薪资
	private String origin;// 该雇员信息的来源

	// 构造方法
	public Employee(String id) {
		this.id = id;
		getDataFromlnfoCenter();
	}

	public String getID() {
		return this.id;
	}

	// 到数据库中取得雇员信息
	private void getDataFromlnfoCenter() {
		// 和数据库建立连接井查询该雇员的信息，将查询结果赋值
		// 给name，department，plone，salary等变量
		// 同时将origin赋值为"From DataBase"
	}
}

class EmployeeCache {
	static private EmployeeCache cache;// 一个Cache实例
	private Hashtable<String, EmployeeRef> employeeRefs;// 用于Chche内容的存储
	private ReferenceQueue<Employee> q;// 垃圾Reference的队列

	// 继承SoftReference，使得每一个实例都具有可识别的标识。
	// 并且该标识与其在HashMap内的key相同。
	private class EmployeeRef extends SoftReference<Employee> {
		private String _key = "";

		public EmployeeRef(Employee em, ReferenceQueue<Employee> q) {
			super(em, q);
			_key = em.getID();
		}
	}

	// 构建一个缓存器实例
	private EmployeeCache() {
		employeeRefs = new Hashtable<String, EmployeeRef>();
		q = new ReferenceQueue<Employee>();
	}

	// 取得缓存器实例
	public static EmployeeCache getInstance() {
		if (cache == null) {
			cache = new EmployeeCache();
		}
		return cache;
	}

	// 以软引用的方式对一个Employee对象的实例进行引用并保存该引用
	private void cacheEmployee(Employee em) {
		cleanCache();// 清除垃圾引用
		EmployeeRef ref = new EmployeeRef(em, q);
		employeeRefs.put(em.getID(), ref);
	}

	// 依据所指定的ID号，重新获取相应Employee对象的实例
	public Employee getEmployee(String ID) {
		Employee em = null;
		// 缓存中是否有该Employee实例的软引用，如果有，从软引用中取得。
		if (employeeRefs.containsKey(ID)) {
			EmployeeRef ref = (EmployeeRef) employeeRefs.get(ID);
			em = (Employee) ref.get();
		}
		// 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
		// 并保存对这个新建实例的软引用
		if (em == null) {
			em = new Employee(ID);
			System.out.println("Retrieve From EmployeeInfoCenter. ID=" + ID);
			this.cacheEmployee(em);
		}
		return em;
	}

	// 清除那些所软引用的Employee对象已经被回收的EmployeeRef对象
	private void cleanCache() {
		EmployeeRef ref = null;
		while ((ref = (EmployeeRef) q.poll()) != null) {
			employeeRefs.remove(ref._key);
		}
	}

	// 清除Cache内的全部内容
	public void clearCache() {
		cleanCache();
		employeeRefs.clear();
		System.gc();
		System.runFinalization();
	}
}

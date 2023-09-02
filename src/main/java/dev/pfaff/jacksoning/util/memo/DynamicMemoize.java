package dev.pfaff.jacksoning.util.memo;

/**
 * Arguments before the function a tracked, arguments after are not.
 */
public interface DynamicMemoize {
	public <R> R get(Computer.By0<R> func);
	public <A, R> R get(Computer.By1<A, R> func, A a);
	public <A, R> R get(A a, Computer.By1<A, R> func);
	public <A, B, R> R get(Computer.By2<A, B, R> func, A a, B b);
	public <A, B, R> R get(A a, Computer.By2<A, B, R> func, B b);
	public <A, B, R> R get(A a, B b, Computer.By2<A, B, R> func);
	public <A, B, C, R> R get(Computer.By3<A, B, C, R> func, A a, B b, C c);
	public <A, B, C, R> R get(A a, Computer.By3<A, B, C, R> func, B b, C c);
	public <A, B, C, R> R get(A a, B b, Computer.By3<A, B, C, R> func, C c);
	public <A, B, C, R> R get(A a, B b, C c, Computer.By3<A, B, C, R> func);
	public <A, B, C, D, R> R get(Computer.By4<A, B, C, D, R> func, A a, B b, C c, D d);
	public <A, B, C, D, R> R get(A a, Computer.By4<A, B, C, D, R> func, B b, C c, D d);
	public <A, B, C, D, R> R get(A a, B b, Computer.By4<A, B, C, D, R> func, C c, D d);
	public <A, B, C, D, R> R get(A a, B b, C c, Computer.By4<A, B, C, D, R> func, D d);
	public <A, B, C, D, R> R get(A a, B b, C c, D d, Computer.By4<A, B, C, D, R> func);
	public <A, B, C, D, E, R> R get(Computer.By5<A, B, C, D, E, R> func, A a, B b, C c, D d, E e);
	public <A, B, C, D, E, R> R get(A a, Computer.By5<A, B, C, D, E, R> func, B b, C c, D d, E e);
	public <A, B, C, D, E, R> R get(A a, B b, Computer.By5<A, B, C, D, E, R> func, C c, D d, E e);
	public <A, B, C, D, E, R> R get(A a, B b, C c, Computer.By5<A, B, C, D, E, R> func, D d, E e);
	public <A, B, C, D, E, R> R get(A a, B b, C c, D d, Computer.By5<A, B, C, D, E, R> func, E e);
	public <A, B, C, D, E, R> R get(A a, B b, C c, D d, E e, Computer.By5<A, B, C, D, E, R> func);
	public <A, B, C, D, E, F, R> R get(Computer.By6<A, B, C, D, E, F, R> func, A a, B b, C c, D d, E e, F f);
	public <A, B, C, D, E, F, R> R get(A a, Computer.By6<A, B, C, D, E, F, R> func, B b, C c, D d, E e, F f);
	public <A, B, C, D, E, F, R> R get(A a, B b, Computer.By6<A, B, C, D, E, F, R> func, C c, D d, E e, F f);
	public <A, B, C, D, E, F, R> R get(A a, B b, C c, Computer.By6<A, B, C, D, E, F, R> func, D d, E e, F f);
	public <A, B, C, D, E, F, R> R get(A a, B b, C c, D d, Computer.By6<A, B, C, D, E, F, R> func, E e, F f);
	public <A, B, C, D, E, F, R> R get(A a, B b, C c, D d, E e, Computer.By6<A, B, C, D, E, F, R> func, F f);
	public <A, B, C, D, E, F, R> R get(A a, B b, C c, D d, E e, F f, Computer.By6<A, B, C, D, E, F, R> func);
	public <A, B, C, D, E, F, G, R> R get(Computer.By7<A, B, C, D, E, F, G, R> func, A a, B b, C c, D d, E e, F f, G g);
	public <A, B, C, D, E, F, G, R> R get(A a, Computer.By7<A, B, C, D, E, F, G, R> func, B b, C c, D d, E e, F f, G g);
	public <A, B, C, D, E, F, G, R> R get(A a, B b, Computer.By7<A, B, C, D, E, F, G, R> func, C c, D d, E e, F f, G g);
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, Computer.By7<A, B, C, D, E, F, G, R> func, D d, E e, F f, G g);
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, Computer.By7<A, B, C, D, E, F, G, R> func, E e, F f, G g);
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, E e, Computer.By7<A, B, C, D, E, F, G, R> func, F f, G g);
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, E e, F f, Computer.By7<A, B, C, D, E, F, G, R> func, G g);
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, E e, F f, G g, Computer.By7<A, B, C, D, E, F, G, R> func);


}
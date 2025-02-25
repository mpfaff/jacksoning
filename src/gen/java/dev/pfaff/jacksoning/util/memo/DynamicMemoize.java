package dev.pfaff.jacksoning.util.memo;

/**
 * Arguments before the function a tracked, arguments after are not.
 */
public interface DynamicMemoize {
	<R> R get(Computer.By0<R> func);
	<A, R> R get(Computer.By1<A, R> func, A a);
	<A, R> R get(A a, Computer.By1<A, R> func);
	<A, B, R> R get(Computer.By2<A, B, R> func, A a, B b);
	<A, B, R> R get(A a, Computer.By2<A, B, R> func, B b);
	<A, B, R> R get(A a, B b, Computer.By2<A, B, R> func);
	<A, B, C, R> R get(Computer.By3<A, B, C, R> func, A a, B b, C c);
	<A, B, C, R> R get(A a, Computer.By3<A, B, C, R> func, B b, C c);
	<A, B, C, R> R get(A a, B b, Computer.By3<A, B, C, R> func, C c);
	<A, B, C, R> R get(A a, B b, C c, Computer.By3<A, B, C, R> func);
	<A, B, C, D, R> R get(Computer.By4<A, B, C, D, R> func, A a, B b, C c, D d);
	<A, B, C, D, R> R get(A a, Computer.By4<A, B, C, D, R> func, B b, C c, D d);
	<A, B, C, D, R> R get(A a, B b, Computer.By4<A, B, C, D, R> func, C c, D d);
	<A, B, C, D, R> R get(A a, B b, C c, Computer.By4<A, B, C, D, R> func, D d);
	<A, B, C, D, R> R get(A a, B b, C c, D d, Computer.By4<A, B, C, D, R> func);
	<A, B, C, D, E, R> R get(Computer.By5<A, B, C, D, E, R> func, A a, B b, C c, D d, E e);
	<A, B, C, D, E, R> R get(A a, Computer.By5<A, B, C, D, E, R> func, B b, C c, D d, E e);
	<A, B, C, D, E, R> R get(A a, B b, Computer.By5<A, B, C, D, E, R> func, C c, D d, E e);
	<A, B, C, D, E, R> R get(A a, B b, C c, Computer.By5<A, B, C, D, E, R> func, D d, E e);
	<A, B, C, D, E, R> R get(A a, B b, C c, D d, Computer.By5<A, B, C, D, E, R> func, E e);
	<A, B, C, D, E, R> R get(A a, B b, C c, D d, E e, Computer.By5<A, B, C, D, E, R> func);
	<A, B, C, D, E, F, R> R get(Computer.By6<A, B, C, D, E, F, R> func, A a, B b, C c, D d, E e, F f);
	<A, B, C, D, E, F, R> R get(A a, Computer.By6<A, B, C, D, E, F, R> func, B b, C c, D d, E e, F f);
	<A, B, C, D, E, F, R> R get(A a, B b, Computer.By6<A, B, C, D, E, F, R> func, C c, D d, E e, F f);
	<A, B, C, D, E, F, R> R get(A a, B b, C c, Computer.By6<A, B, C, D, E, F, R> func, D d, E e, F f);
	<A, B, C, D, E, F, R> R get(A a, B b, C c, D d, Computer.By6<A, B, C, D, E, F, R> func, E e, F f);
	<A, B, C, D, E, F, R> R get(A a, B b, C c, D d, E e, Computer.By6<A, B, C, D, E, F, R> func, F f);
	<A, B, C, D, E, F, R> R get(A a, B b, C c, D d, E e, F f, Computer.By6<A, B, C, D, E, F, R> func);
	<A, B, C, D, E, F, G, R> R get(Computer.By7<A, B, C, D, E, F, G, R> func, A a, B b, C c, D d, E e, F f, G g);
	<A, B, C, D, E, F, G, R> R get(A a, Computer.By7<A, B, C, D, E, F, G, R> func, B b, C c, D d, E e, F f, G g);
	<A, B, C, D, E, F, G, R> R get(A a, B b, Computer.By7<A, B, C, D, E, F, G, R> func, C c, D d, E e, F f, G g);
	<A, B, C, D, E, F, G, R> R get(A a, B b, C c, Computer.By7<A, B, C, D, E, F, G, R> func, D d, E e, F f, G g);
	<A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, Computer.By7<A, B, C, D, E, F, G, R> func, E e, F f, G g);
	<A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, E e, Computer.By7<A, B, C, D, E, F, G, R> func, F f, G g);
	<A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, E e, F f, Computer.By7<A, B, C, D, E, F, G, R> func, G g);
	<A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, E e, F f, G g, Computer.By7<A, B, C, D, E, F, G, R> func);

	
}


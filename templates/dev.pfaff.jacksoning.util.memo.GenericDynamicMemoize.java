package dev.pfaff.jacksoning.util.memo;

import dev.pfaff.jacksoning.Jacksoning;
import org.slf4j.event.Level;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

public final class GenericDynamicMemoize implements DynamicMemoize {
	public static final GenericDynamicMemoize[] EMPTY_GENERIC_DIFFING_COMPUTERS = new GenericDynamicMemoize[0];

	private static final Level LOG_LEVEL_DIRTY = Level.DEBUG;

	private static final List<String> ARG_FIELDS = List.of({{ char_range(from='a', n=DIFFING_COMPUTER_MAX_ARITY) | delimit(pre='"', sep='", "', post='"') }});
	private static final List<MethodHandle> GETTERS = ARG_FIELDS.stream().map(name -> {
		try {
			return MethodHandles.lookup().findGetter(GenericDynamicMemoize.class, name, Object.class);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
	}).toList();
	private static final List<MethodHandle> SETTERS = ARG_FIELDS.stream().map(name -> {
		try {
			return MethodHandles.lookup().findSetter(GenericDynamicMemoize.class, name, Object.class);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
	}).toList();

	@SuppressWarnings("unused")
	private Object {{ char_range(from='a', n=DIFFING_COMPUTER_MAX_ARITY) | delimit(sep=", ") }};
	private Computer<?> func;
	private Object result;

	public void reset() {
		{% for c in char_range(from='a', n=DIFFING_COMPUTER_MAX_ARITY) -%}
		{{ c }} = null;
		{% endfor -%}
		func = null;
		result = null;
	}

	private boolean funcDirty(Computer func) {
		if (this.func == null || this.func.getClass() != func.getClass()) {
			Jacksoning.LOGGER.log(LOG_LEVEL_DIRTY, () -> "Computer is dirty: " + this.func + " != " + func);
			this.func = func;
			return true;
		} else {
			return false;
		}
	}

	private boolean argDirty(int argI, Object arg) {
		Object existing;
		var getter = GETTERS.get(argI);
		try {
			existing = getter.invokeExact(this);
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
		if (!Objects.equals(existing, arg)) {
			Jacksoning.LOGGER.log(LOG_LEVEL_DIRTY,
								  () -> "Argument ." + ((int) 'a' + argI) + " is dirty: " + existing + " != " + arg);
			var setter = SETTERS.get(argI);
			try {
				setter.invokeExact(this, arg);
			} catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			return true;
		} else {
			return false;
		}
	}

	// this *should* be inlined by the JVM into each of the get methods, if not further. From there, the argN checks
	// will be constant-folded.
	private <{{ char_range(from='A', n=DIFFING_COMPUTER_MAX_ARITY) | delimit(sep=", ", post=", ") -}} R> R update(Computer<R> func, int argN {%- for c in char_range(from='A', n=DIFFING_COMPUTER_MAX_ARITY) %}, {{ c }} {{ c | lower }} {%- endfor -%}) {
		boolean dirty = funcDirty(func);
		{%- for n in range(until=DIFFING_COMPUTER_MAX_ARITY) %}
		if (argN >= {{ n+1 }}) dirty |= argDirty({{ n }}, {{ char_add(c='a', i=n) }});
		{%- endfor %}
		if (dirty) {
			// dispatch to the appropriate compute function
			// this *should* be constant-folded too.
			R result = switch (func) {
				case Computer.By0<R> by -> by.compute();
				{%- for n in range(from=1, until=DIFFING_COMPUTER_MAX_ARITY+1) %}
				case @SuppressWarnings("rawtypes") Computer.By{{ n }} by -> (R) by.compute({{ char_range(from='a', n=n) | delimit(sep=", ") }});
				{%- endfor %}
			};
			this.result = result;
			return result;
		}
		return (R) result;
	}

	{%- for total in range(until=DIFFING_COMPUTER_MAX_ARITY+1) %}
	{%- for tracked in range(until=total+1) %}
	{%- set untracked = total - tracked %}

	@Override
	public <{{ char_range(from='A', n=total) | delimit(sep=", ", post=", ") }}R> R get({% for c in char_range(from='A', n=tracked) %}{{ c }} {{ c | lower }}, {% endfor %}Computer.By{{ total }}<{{ char_range(from='A', n=total) | delimit(sep=", ", post=", ") }}R> func{% for c in char_range(from=(char_add(c='A', i=tracked)), n=untracked) %}, {{ c }} {{ c | lower }}{% endfor %}) {
		return update(func, {{ tracked }} {%- for i in range(until=DIFFING_COMPUTER_MAX_ARITY) %}, {% if i < total %}{{ char_add(c='a', i=i) }}{% else %}null{% endif %} {%- endfor %});
	}
	{%- endfor %}
	{%- endfor %}

	@Override
	public String toString() {
		return "GenericDynamicMemoize["
			{% for c in char_range(from='a', n=DIFFING_COMPUTER_MAX_ARITY) -%}
			+ "{{ c }}=" + {{ c }} + ", "
			{% endfor -%}
			+ "func=" + func
			+ ", " + "result=" + result
			+ ']';
	}
}

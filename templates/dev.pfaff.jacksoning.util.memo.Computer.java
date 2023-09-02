package dev.pfaff.jacksoning.util.memo;

public sealed interface Computer<R> {
	{%- for i in range(until=DIFFING_COMPUTER_MAX_ARITY+1) %}
	@FunctionalInterface
	public non-sealed interface By{{ i }}<{{ char_range(from='A', n=i) | delimit(sep=", ", post=", ") }}R> extends Computer<R> {
		R compute({% for c in char_range(from='A', n=i) %}{% if c != 'A' %}, {% endif %}{{ c }} {{ c | lower }}{% endfor %});
	}
{% endfor -%}
}


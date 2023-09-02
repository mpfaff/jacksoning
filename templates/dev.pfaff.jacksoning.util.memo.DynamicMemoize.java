package dev.pfaff.jacksoning.util.memo;

/**
 * Arguments before the function a tracked, arguments after are not.
 */
public interface DynamicMemoize {
	{%- for total in range(until=DIFFING_COMPUTER_MAX_ARITY+1) %}
	{%- for tracked in range(until=total+1) %}
	{%- set untracked = total - tracked %}
	public <{{ char_range(from='A', n=total) | delimit(sep=", ", post=", ") }}R> R get({% for c in char_range(from='A', n=tracked) %}{{ c }} {{ c | lower }}, {% endfor %}Computer.By{{ total }}<{{ char_range(from='A', n=total) | delimit(sep=", ", post=", ") }}R> func{% for c in char_range(from=(char_add(c='A', i=tracked)), n=untracked) %}, {{ c }} {{ c | lower }}{% endfor %});
	{%- endfor %}
	{%- endfor %}

	{#public interface Args<A, B, C, D, E, F, G> {
		{%- for c in char_range(from='A', n=DIFFING_COMPUTER_MAX_ARITY+1) %}
		{{ c }} {{ c | lower }}();
	}#}
}


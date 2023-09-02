	{%- for total in range(until=DIFFING_COMPUTER_MAX_ARITY+1) %}
	{%- for tracked in range(until=total+1) %}
	{%- set untracked = total - tracked %}
	@Override
	public <{{ char_range(from='A', n=total) | delimit(sep=", ", post=", ") }}R> R get({% for c in char_range(from='A', n=tracked) %}{{ c }} {{ c | lower }}, {% endfor %}Computer.By{{ total }}<{{ char_range(from='A', n=total) | delimit(sep=", ", post=", ") }}R> func{% for c in char_range(from=(char_add(c='A', i=tracked)), n=untracked) %}, {{ c }} {{ c | lower }}{% endfor %}) {
		return update(func, {{ tracked }} {%- for i in range(until=DIFFING_COMPUTER_MAX_ARITY) %}, {% if i < total %}{{ char_add(c='a', i=i) }}{% else %}null{% endif %} {%- endfor %});
	}
	{% endfor %}
	{%- endfor %}

package dev.pfaff.jacksoning.util.memo;

import dev.pfaff.jacksoning.util.OpenArrayList;

public final class DiffingComputerList {
	private final OpenArrayList<GenericDynamicMemoize> entries = OpenArrayList.wrap(GenericDynamicMemoize.EMPTY_GENERIC_DIFFING_COMPUTERS);

	private GenericDynamicMemoize getEntry(int i) {
		if (i == entries.size()) {
			var entry = new GenericDynamicMemoize();
			entries.add(entry);
			return entry;
		}
		return entries.get(i);
	}

	{%- for total in range(until=DIFFING_COMPUTER_MAX_ARITY) %}
	{%- for tracked in range(until=total+1) %}
	{%- set untracked = total - tracked %}

	public <{{ char_range(from='A', n=total) | delimit(sep=", ", post=", ") }}R> R get(int i, {% for c in char_range(from='A', n=tracked) %}{{ c }} {{ c | lower }}, {% endfor %}Computer.By{{ total+1 }}<{{ char_range(from='A', n=total) | delimit(sep=", ", post=", ") }}Integer, R> func{% for c in char_range(from=(char_add(c='A', i=tracked)), n=untracked) %}, {{ c }} {{ c | lower }}{% endfor %}) {
		return getEntry(i).get({% for i in range(until=tracked) %}{{ char_add(c='a', i=i) }}, {% endfor -%} func {%- for i in range(until=untracked) %}, {{ char_add(c='a', i=tracked+i) }}{% endfor %}, i);
	}
	{%- endfor %}
	{%- endfor %}

	public void truncate(int length) {
		var arr = entries.a();
		for (int i = length; i < arr.length; i++) {
			var entry = arr[i];
			if (entry != null) entry.reset();
		}
	}
}

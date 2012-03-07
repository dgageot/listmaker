/*
 * This file is part of ListMaker.
 *
 * Copyright (C) 2012
 * "David Gageot" <david@gageot.net>,
 *
 * ListMaker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ListMaker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ListMaker.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.gageot.listmaker;

import com.google.common.base.*;
import com.google.common.collect.*;

import java.util.*;

public class ListMaker<T> implements Iterable<T> {
	private final Iterable<T> values;

	private ListMaker(Iterable<T> values) {
		this.values = values;
	}

	public static <T> ListMaker<T> with(Iterable<T> values) {
		if (values instanceof ListMaker<?>) {
			return (ListMaker<T>) values;
		}
		return new ListMaker<T>(values);
	}

	@SuppressWarnings("unchecked")
	public static <T> ListMaker<T> with() {
		return new ListMaker<T>(Collections.<T>emptyList());
	}

	public static <T> ListMaker<T> with(T... values) {
		return new ListMaker<T>(Arrays.asList(values));
	}

	public static <V, T> Predicate<? super T> where(Function<? super T, ? extends V> transform, Predicate<? super V> condition) {
		return Predicates.compose(condition, transform);
	}

	public static <V, T> Predicate<? super T> whereEquals(Function<? super T, ? extends V> transform, V valueToCompareWith) {
		return where(transform, Predicates.equalTo(valueToCompareWith));
	}

	public ListMaker<T> only(Predicate<? super T> condition) {
		return new ListMaker<T>(Iterables.filter(values, condition));
	}

	public ListMaker<T> exclude(Predicate<? super T> condition) {
		return only(Predicates.not(condition));
	}

	public ListMaker<T> exclude(T... valuesToExclude) {
		return exclude(Arrays.asList(valuesToExclude));
	}

	public ListMaker<T> exclude(Collection<T> valuesToExclude) {
		return exclude(Predicates.in(valuesToExclude));
	}

	public T first() {
		return values.iterator().next();
	}

	public T first(Predicate<? super T> condition) {
		return Iterables.find(values, condition);
	}

	public T firstOrDefault(T defaultValue) {
		return Iterables.getFirst(values, defaultValue);
	}

	public T firstOrDefault(Predicate<? super T> condition, T defaultValue) {
		return Iterables.find(values, condition, defaultValue);
	}

	public boolean contains(Predicate<? super T> condition) {
		return Iterables.any(values, condition);
	}

	public int count(Predicate<? super T> condition) {
		return Iterables.size(Iterables.filter(values, condition));
	}

	public ListMaker<T> sortOn(Ordering<? super T> ordering) {
		return new ListMaker<T>(ordering.sortedCopy(values));
	}

	public ListMaker<T> sortOn(Comparator<? super T> comparator) {
		return sortOn(Ordering.from(comparator));
	}

	public ListMaker<T> sortOn(Function<? super T, ? extends Comparable<?>> transform) {
		return sortOn(Ordering.natural().onResultOf(transform));
	}

	public ListMaker<T> notNulls() {
		return only(Predicates.<T>notNull());
	}

	public <R> ListMaker<R> to(Function<? super T, R> transform) {
		return new ListMaker<R>(Iterables.transform(values, transform));
	}

	public <R, C extends Iterable<R>> ListMaker<R> flatMap(Function<? super T, C> transform) {
		return new ListMaker<R>(Iterables.concat(Iterables.transform(values, transform)));
	}

	public T max(Ordering<? super T> ordering) {
		return ordering.max(values);
	}

	public T min(Ordering<? super T> ordering) {
		return ordering.min(values);
	}

	public <V extends Comparable<V>> T maxOnResultOf(Function<? super T, V> transform) {
		return Ordering.natural().onResultOf(transform).max(values);
	}

	public <V extends Comparable<V>> T minOnResultOf(Function<? super T, V> transform) {
		return Ordering.natural().onResultOf(transform).min(values);
	}

	public <C extends Collection<T>> C copyTo(C destination) {
		Iterables.addAll(destination, values);
		return destination;
	}

	public List<T> toList() {
		return Lists.newArrayList(values);
	}

	public ImmutableList<T> toImmutableList() {
		return ImmutableList.copyOf(values);
	}

	public String join(String separator) {
		return Joiner.on(separator).join(values);
	}

	public T[] toArray(Class<T> type) {
		return Iterables.toArray(values, type);
	}

	public Set<T> toSet() {
		return Sets.newHashSet(values);
	}

	public <R> Set<R> toSet(Function<? super T, R> transform) {
		return Sets.newHashSet(Iterables.transform(values, transform));
	}

	public TreeSet<T> toTreeSet() {
		return copyTo(new TreeSet<T>());
	}

	public TreeSet<T> toTreeSet(Comparator<? super T> comparator) {
		return copyTo(new TreeSet<T>(comparator));
	}

	public <R> TreeSet<R> toTreeSet(Function<? super T, R> transform, Comparator<? super R> ordering) {
		TreeSet<R> set = new TreeSet<R>(ordering);
		Iterables.addAll(set, Iterables.transform(values, transform));
		return set;
	}

	public <R extends Comparable<R>> TreeSet<R> toTreeSet(Function<? super T, R> transform) {
		return toTreeSet(transform, null);
	}

	public ImmutableSet<T> toImmutableSet() {
		return ImmutableSet.copyOf(values);
	}

	public <R> ImmutableSet<R> toImmutableSet(Function<? super T, R> transform) {
		return ImmutableSet.copyOf(Iterables.transform(values, transform));
	}

	public T getLast() {
		return Iterables.getLast(values);
	}

	public int size() {
		return Iterables.size(values);
	}

	public <K> Map<K, T> indexBy(Function<? super T, ? extends K> toKey) {
		Map<K, T> map = Maps.newHashMap();

		for (T value : values) {
			map.put(toKey.apply(value), value);
		}

		return map;
	}

	@Override
	public Iterator<T> iterator() {
		return values.iterator();
	}

	public boolean isEmpty() {
		return Iterables.isEmpty(values);
	}

	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof Iterable<?> && Iterables.elementsEqual(this, (Iterable<?>) o);
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public String toString() {
		return values.toString();
	}
}

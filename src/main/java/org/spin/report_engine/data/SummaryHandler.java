/************************************************************************************
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                     *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program. If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.report_engine.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.spin.report_engine.format.PrintFormatItem;

/**
 * This class have all need for manage row summary and groups
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class SummaryHandler {
	private List<PrintFormatItem> groupedItems;
	private List<PrintFormatItem> summarizedItems;
	private Map<Integer, Map<Row, Map<Integer, SummaryFunction>>> summary;

	private SummaryHandler(List<PrintFormatItem> printFormatItems) {
		groupedItems = printFormatItems.stream().filter(item -> item.isGroupBy()).sorted(Comparator.comparing(PrintFormatItem::getSortSequence)).collect(Collectors.toList());
		summarizedItems = printFormatItems.stream().filter(printItem -> {
			return printItem.isAveraged() || printItem.isCounted() || printItem.isMaxCalc() || printItem.isMinCalc() || printItem.isSummarized() || printItem.isVarianceCalc();
		}).collect(Collectors.toList());
		summary = new HashMap<Integer, Map<Row, Map<Integer, SummaryFunction>>>();
	}

	public SummaryHandler addRow(Row row) {
		groupedItems.forEach(groupItem -> {
			Row keyRow = Row.newInstance().withLevel(groupItem.getSortSequence());
			groupedItems.stream().filter(item -> item.getSortSequence() <= groupItem.getSortSequence()).forEach(item ->{
				keyRow.withCell(item.getPrintFormatItemId(), row.getCell(item.getPrintFormatItemId()));
			});
			//	Accumulate the per-group column totals (the only summary consumed by getAsRows()).
			//	Each SummaryFunction already keeps every statistic (sum/avg/count/min/max/...), so a
			//	single instance per column is enough; the previous per-function parallel maps were
			//	never read and have been removed.
			Map<Row, Map<Integer, SummaryFunction>> groupTotals =
				summary.computeIfAbsent(groupItem.getPrintFormatItemId(), key -> new HashMap<Row, Map<Integer, SummaryFunction>>());
			Map<Integer, SummaryFunction> columnTotals =
				groupTotals.computeIfAbsent(keyRow, key -> new HashMap<Integer, SummaryFunction>());
			summarizedItems.forEach(sumItem -> {
				addValue(sumItem.getPrintFormatItemId(), columnTotals, row.getCell(sumItem.getPrintFormatItemId()));
			});
		});
		return this;
	}

	private void addValue(int key, Map<Integer, SummaryFunction> columnTotals, Cell cell) {
		columnTotals.computeIfAbsent(key, k -> SummaryFunction.newInstance()).addValue(cell.getFunctionValue());
	}
	
	public static SummaryHandler newInstance(List<PrintFormatItem> groupedItems) {
		return new SummaryHandler(groupedItems);
	}

	public List<PrintFormatItem> getGroupedItems() {
		return groupedItems;
	}

	public List<PrintFormatItem> getSummarizedItems() {
		return summarizedItems;
	}

	public Map<Integer, Map<Row, Map<Integer, SummaryFunction>>> getSummary() {
		return summary;
	}
	
	public List<Row> getAsRows() {
		List<Row> rows = new ArrayList<Row>();
		AtomicBoolean isFirst = new AtomicBoolean(true);
		groupedItems.forEach(groupItem -> {
			Map<Row, Map<Integer, SummaryFunction>> groupTotals = Optional.ofNullable(summary.get(groupItem.getPrintFormatItemId())).orElse(new HashMap<Row, Map<Integer,SummaryFunction>>());
			groupTotals.keySet().forEach(groupValueRow -> {
				Map<Integer, SummaryFunction> summaryValue = groupTotals.get(groupValueRow);
				summarizedItems.forEach(sumItem -> {
					SummaryFunction function = summaryValue.get(sumItem.getPrintFormatItemId());
					groupValueRow.withCell(sumItem.getPrintFormatItemId(), Cell.newInstance().withValue(function.getValue(SummaryFunction.F_SUM)).withFunction(function));
				});
				rows.add(groupValueRow.withSummaryRow(true));
			});
			isFirst.set(false);
		});
		return rows;
	}
	
	@Override
	public String toString() {
		return "SummaryHandler [groupedItems=" + groupedItems + ", summarizedItems=" + summarizedItems + ", summary="
				+ summary + "]";
	}
}

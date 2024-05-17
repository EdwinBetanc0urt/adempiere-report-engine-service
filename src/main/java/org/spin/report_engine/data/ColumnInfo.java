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

/**
 * Cell Information can be used to represent the data and some attributes like:
 * <li>Color: A color definition
 * <li>Style: Represent the style for it
 * <li>Code: Represent the unique code or ID for column
 * <li>Title: Represent the column title
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class ColumnInfo {
	private String color;
	private String style;
	private String code;
	private Object title;
	
	private ColumnInfo() {
		
	}
	
	public static ColumnInfo newInstance() {
		return new ColumnInfo();
	}
	
	public String getColor() {
		return color;
	}
	
	public ColumnInfo withColor(String color) {
		this.color = color;
		return this;
	}
	
	public String getStyle() {
		return style;
	}
	
	public ColumnInfo withStyle(String style) {
		this.style = style;
		return this;
	}
	
	public String getCode() {
		return code;
	}
	
	public ColumnInfo withCode(String code) {
		this.code = code;
		return this;
	}
	
	public Object getTitle() {
		return title;
	}
	
	public ColumnInfo withTitle(Object title) {
		this.title = title;
		return this;
	}

	@Override
	public String toString() {
		return "ColumnInfo [color=" + color + ", style=" + style + ", code=" + code + ", title=" + title + "]";
	}
}
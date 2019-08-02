/** 
 * This code is copyright (c) Mathias Markl 2016
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.kevinersoy.giphysearch.model.search;

import com.google.gson.annotations.SerializedName;
import com.kevinersoy.giphysearch.model.common.Meta;
import com.kevinersoy.giphysearch.model.giphy.GiphyRandom;

/**
 * This class represents a single search response.
 *
 * @author Mathias Markl
 */
public class SearchRandom {
    @SerializedName("data")
    private GiphyRandom data;

    @SerializedName("meta")
    private Meta meta;

    /**
     * Returns the data.
     * 
     * <p>
     * "data": { ... }
     * 
     * @return The data.
     */
    public GiphyRandom getData() {
	return data;
    }

    /**
     * Sets the data.
     * 
     * @param data
     *            the data
     */
    public void setData(GiphyRandom data) {
	this.data = data;
    }

    /**
     * Returns the meta data.
     * 
     * <p>
     * "meta": { ... }
     * 
     * @return the meta data.
     */
    public Meta getMeta() {
	return meta;
    }

    /**
     * Sets the meta data.
     * 
     * @param meta
     *            the meta data
     */
    public void setMeta(Meta meta) {
	this.meta = meta;
    }

    @Override
    public String toString() {
	String outputString = "SearchRandom [";
	outputString += "\n  " + data;
	outputString += "\n  " + meta + "\n]";
	return outputString;
    }
}

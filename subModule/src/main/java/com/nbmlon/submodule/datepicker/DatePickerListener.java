package com.nbmlon.submodule.datepicker;

import org.joda.time.DateTime;

/**
 * Created by jhonn on 02/03/2017.
 * Modified by nbmLon99 on 18/09/2022.
 */
public interface DatePickerListener {
    void onDateSelected(DateTime dateSelected);
}
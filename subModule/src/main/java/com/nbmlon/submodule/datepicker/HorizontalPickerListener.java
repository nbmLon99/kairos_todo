package com.nbmlon.submodule.datepicker;

/**
 * Created by jhonn on 02/03/2017.
 * Modified by nbmLon99 on 18/09/2022
 */
public interface HorizontalPickerListener {
    void onStopDraggingPicker();
    void onDraggingPicker();
    void onDateSelected(Day item);
}
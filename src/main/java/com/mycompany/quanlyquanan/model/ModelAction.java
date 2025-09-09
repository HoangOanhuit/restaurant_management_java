
package com.mycompany.quanlyquanan.model;


import com.mycompany.quanlyquanan.view.Pagination.EventAction;

public class ModelAction {
    private ModelVoucher voucher;
    private EventAction event;

    // Constructor không tham số
    public ModelAction() {

    }
    public ModelAction(ModelVoucher voucher, EventAction event) {
        this.voucher = voucher;
        this.event = event;
    }

    // Getter & Setter

    public ModelVoucher getVoucher() {
        return voucher;
    }

    public void setVoucher(ModelVoucher voucher) {
        this.voucher = voucher;
    }

    public EventAction getEvent() {
        return event;
    }

    public void setEvent(EventAction event) {
        this.event = event;
    }
}

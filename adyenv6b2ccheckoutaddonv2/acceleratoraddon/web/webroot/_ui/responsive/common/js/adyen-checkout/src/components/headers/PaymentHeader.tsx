import {BaseHeader, BaseHeaderProps} from "./BaseHeader";
import {routes} from "../../router/routes";


const title = "Payment & Billing Address";
const redirectUrl = routes.paymentMethod;
export class PaymentHeader extends BaseHeader {

    constructor(props: BaseHeaderProps) {
        super(props, title, redirectUrl);
    }

}
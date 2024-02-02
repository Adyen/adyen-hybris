import {BaseHeader, BaseHeaderProps} from "./BaseHeader";
import {routes} from "../../router/routes";

const title = "Shipment/Pick Up Location";
const redirectUrl = routes.shippingAddress;
export class ShippingAddressHeader extends BaseHeader {

    constructor(props: BaseHeaderProps) {
        super(props, title, redirectUrl);
    }

}
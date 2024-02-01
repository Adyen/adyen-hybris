import {BaseHeader, BaseHeaderProps} from "./BaseHeader";
import {routes} from "../../router/routes";

const title = "Shipping Method";
const redirectUrl = routes.shippingMethod;

export class ShippingMethodHeader extends BaseHeader {

    constructor(props: BaseHeaderProps) {
        super(props, title, redirectUrl);
    }

}
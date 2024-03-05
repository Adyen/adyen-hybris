import {BaseHeader, BaseHeaderProps} from "./BaseHeader";
import {routes} from "../../router/routes";

const title = "Final Review";
const redirectUrl = routes.thankYouPage;
export class FinalReviewHeader extends BaseHeader {

    constructor(props: BaseHeaderProps) {
        super(props, title, redirectUrl);
    }

}
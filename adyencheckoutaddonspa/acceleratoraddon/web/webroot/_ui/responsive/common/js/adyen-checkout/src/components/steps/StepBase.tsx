import {CartDataService} from "../../service/cartDataService";
import React from "react";


export class StepBase extends React.Component<{}, null> {

    // private readonly notificationsWithRedirect: Notification[]

    constructor(props: {}) {
        super(props);
        // this.notificationsWithRedirect = store.getState().notifications.filter((notification) => notification.isRedirect)
    }

    componentDidMount() {
        CartDataService.fetchCartData();
    }

    // componentWillUnmount() {
    //     store.dispatch({type: "notifications/removeNotificationsWithoutRedirect"})
    //     if (this.notificationsWithRedirect.length > 0) {
    //         store.dispatch({type: "notifications/removeNotificationsFromList", payload: this.notificationsWithRedirect})
    //     }
    //     console.log("StepBase unmount.")
    // }


}
import React from "react";
import {translationsStore} from "../../store/translationsStore";
import {PaymentPending} from "./PaymentPending";
import {PaymentStatus} from "../../types/paymentStatus";
import {PaymentSuccess} from "./PaymentSuccess";
import {PaymentFailed} from "./PaymentFailed";
import {PaymentRejected} from "./PaymentRejected";
import {PaymentTimeout} from "./PaymentTimeout";
import {PaymentStatusService} from "../../service/paymentStatusService";
import {isNotEmpty} from "../../util/stringUtil";

interface Props {
    orderCode: string
}

interface State {
    numberOfStatusChecks: number,
    paymentStatus: PaymentStatus
}

export class ThankYouPage extends React.Component<Props, State> {
    private statusRequestInterval = 2 * 1000; // 2 * 1000ms
    private numberOfAllStatusChecks = 60; // 120s -> 60 * statusRequestInterval
    private timer: NodeJS.Timeout;

    constructor(props: Props) {
        super(props);
        this.state = {
            numberOfStatusChecks: 0,
            paymentStatus: "waiting"
        }
    }

    componentDidMount() {
        this.timer = setInterval(() => this.checkStatus(), this.statusRequestInterval);
    }

    componentWillUnmount() {
        clearInterval(this.timer);
    }


    private async checkStatus() {
        if (this.state.numberOfStatusChecks + 1 <= this.numberOfAllStatusChecks) {
            let paymentStatus = await PaymentStatusService.fetchPaymentStatus(this.props.orderCode);

            if (isNotEmpty(paymentStatus)) {
                this.setState((state): State => {
                    return {
                        ...state,
                        paymentStatus: paymentStatus
                    }
                })

                if (paymentStatus !== "waiting") {
                    clearInterval(this.timer)
                }
            }

            this.setState((state): State => {
                return {
                    ...state,
                    numberOfStatusChecks: state.numberOfStatusChecks + 1
                }
            })
        } else {
            this.setState({paymentStatus: "timeout"})
            clearInterval(this.timer);
        }
    }

    private renderPaymentStatus(): React.JSX.Element {
        switch (this.state.paymentStatus) {
            case "waiting":
                return <PaymentPending/>
            case "completed":
                return <PaymentSuccess/>
            case "error":
                return <PaymentFailed/>
            case "rejected":
                return <PaymentRejected/>
            case "timeout":
            case "unknown":
                return <PaymentTimeout/>
        }
    }

    render() {
        return (
            <div className="checkout-success">
                <div className="checkout-success__body">
                    <div className="checkout-success__body__headline">
                        {translationsStore.get("checkout.orderConfirmation.thankYouForOrder")}
                    </div>
                    <p>{translationsStore.get("text.account.order.orderNumberLabel")}<strong> {this.props.orderCode}</strong>
                    </p>
                    {this.renderPaymentStatus()}
                </div>
            </div>)
    }
}
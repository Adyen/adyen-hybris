import {CartData} from "../../types/cartData";
import React from "react";
import {Price} from "../common/Price";
import {formatStringWithPlaceholders} from "../../util/stringUtil";

interface Props {
    cartData: CartData
    showTax: boolean
}

export class OrderTotals extends React.Component<Props, null> {

    private renderDiscounts(): React.JSX.Element {
        if (this.props.cartData.totalDiscounts && (this.props.cartData.totalDiscounts.value > 0)) {
            return (
                <div className="subtotals__item--state-discount">
                    Order Discounts:
                    <span>
                        <Price price={this.props.cartData.totalDiscounts} displayNegativeForDiscount={true}/>
                    </span>
                </div>
            )
        }

        return <></>
    }

    private renderDelivery(): React.JSX.Element {
        if (this.props.cartData.deliveryCost && (this.props.cartData.deliveryCost.value > 0)) {
            return (
                <div className="shipping">
                    Delivery:
                    <span>
                        <Price price={this.props.cartData.deliveryCost} displayFreeForZero={true}/>
                    </span>
                </div>
            )
        }

        return <></>
    }

    private renderTax(): React.JSX.Element {
        if (this.props.cartData.totalTax && this.props.showTax && this.props.cartData.net
            && this.props.cartData.totalTax.value > 0) {
            return (
                <div className="tax">
                    Tax:
                    <span>
                        <Price price={this.props.cartData.totalTax}/>
			        </span>
                </div>
            )
        }

        return <></>
    }

    private renderOrderTotal(): React.JSX.Element {
        if (this.props.showTax) {
            if (this.props.cartData.totalPriceWithTax) {
                return <Price price={this.props.cartData.totalPriceWithTax}/>
            }
        } else {
            if (this.props.cartData.totalPrice) {
                return <Price price={this.props.cartData.totalPrice}/>
            }
        }
        return <> </>
    }

    private renderRealTotals(): React.JSX.Element {
        if (!this.props.cartData.net && this.props.cartData.totalTax) {
            return (
                <div className="realTotals">
                    <p>
                        {formatStringWithPlaceholders("Your order includes {0} tax.", this.props.cartData.totalTax.formattedValue)}
                    </p>
                </div>
            )
        }

        if (this.props.cartData.net && !this.props.showTax) {
            return (
                <div className="realTotals">
                    <p>
                        *No taxes are included in the total
                    </p>
                </div>
            )
        }

        return <></>
    }

    private shouldRenderEmpty() {
        return !this.props.cartData || !this.props.cartData.subTotal
    }

    render() {
        if (this.shouldRenderEmpty()) {
            return <></>
        }

        return (
            <div className="subtotals">
                <div className="subtotal">
                    Subtotal:
                    <span>
                        <Price price={this.props.cartData.subTotal}/>
            </span>
                </div>
                {this.renderDiscounts()}
                {this.renderDelivery()}
                {this.renderTax()}
                <div className="totals">
                    Order Total
                    <span>
                        {this.renderOrderTotal()}
                    </span>
                </div>
                {this.renderRealTotals()}
            </div>
        )

    }
}
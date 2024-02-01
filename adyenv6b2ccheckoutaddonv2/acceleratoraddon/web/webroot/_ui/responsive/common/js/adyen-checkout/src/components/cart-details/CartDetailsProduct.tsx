import React from "react";
import {OrderEntryData} from "../../types/cartData";
import {ImageData} from "../../types/cartData";
import {Price} from "../common/Price";

interface Props {
    entryData: OrderEntryData
}

export class CartDetailsProduct extends React.Component<Props, null> {

    private getPrimaryImage(): ImageData {
        let filteredImages = this.props.entryData.product.images.filter(img => img.imageType === "PRIMARY");
        if (filteredImages.length > 0) {
            return filteredImages[0]
        }
        return {
            url: "",
            altText: "",
            imageType: ""
        }
    }

    render() {
        if (this.props.entryData.deliveryPointOfService || !this.props.entryData) {
            return (
                <></>
            );
        }

        let primaryImage = this.getPrimaryImage();

        return (
            <li className="checkout-order-summary-list-items">
                <div className="thumb">
                    <a href={this.props.entryData.product.url}>
                        <img src={primaryImage.url} alt={primaryImage.altText}/>
                    </a>
                </div>
                <div className="price"><Price price={this.props.entryData.totalPrice} displayFreeForZero={true}/></div>
                <div className="details">
                    <div className="name">
                        <a href={this.props.entryData.product.url}>{this.props.entryData.product.name}</a>
                    </div>
                    <div>
                        <span className="label-spacing">Item Price:</span>
                        <Price price={this.props.entryData.basePrice} displayFreeForZero={true}/>
                    </div>
                    <div className="qty">
                        <span>Qty: {this.props.entryData.quantity}</span>
                    </div>
                    <div>
                        {/*TODO: promotions*/}
                    </div>
                </div>
            </li>
        )

    }
}
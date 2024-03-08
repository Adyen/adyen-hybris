import React from "react";

interface Props {
    orderCode: string
}


export class ThankYouPage extends React.Component<Props, null> {
    render() {
        return (
            <div>
            <span>
                Order number: {this.props.orderCode}
            </span>
            </div>)
    }
}
import React from "react";


interface InputTextProps {
    fieldName: string
    value?: string
    onChange: (value: string) => void
}

export class InputText extends React.Component<InputTextProps, null> {

    render() {
        return (
            <div className={"form-group"}>
                <label className={"form-input_name control-label"}>{this.props.fieldName}</label>
                <input className={"form-input_input form-control"} type={"text"}
                       onChange={(event) => this.props.onChange(event.target.value)} value={this.props.value} />
            </div>
        )
    }
}
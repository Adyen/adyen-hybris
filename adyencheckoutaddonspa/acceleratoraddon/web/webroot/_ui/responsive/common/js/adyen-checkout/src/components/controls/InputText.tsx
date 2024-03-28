import React from "react";
import {isNotEmpty} from "../../util/stringUtil";


interface InputTextProps {
    testId?: string
    fieldName: string
    value?: string
    onChange: (value: string) => void
}

export class InputText extends React.Component<InputTextProps, null> {

    private renderInput(): React.JSX.Element {
        if (isNotEmpty(this.props.testId)) {
            return <input id={this.props.testId}
                          className={"form-input_input form-control"} type={"text"}
                          onChange={(event) => this.props.onChange(event.target.value)} value={this.props.value}/>
        }

        return <input className={"form-input_input form-control"} type={"text"}
                      onChange={(event) => this.props.onChange(event.target.value)} value={this.props.value}/>

    }

    render() {
        return (
            <div className={"form-group"}>
                <label className={"form-input_name control-label"}>{this.props.fieldName}</label>
                {this.renderInput()}
            </div>
        )
    }
}
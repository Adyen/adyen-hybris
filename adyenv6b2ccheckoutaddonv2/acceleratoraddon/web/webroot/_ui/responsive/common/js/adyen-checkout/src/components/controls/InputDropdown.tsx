import React from "react";
import {CodeValueItem} from "../../reducers/types";

interface InputDropdownProps {
    values: CodeValueItem[]
    fieldName: string
    selectedValue?: string
    onChange?: (value: string) => void
}

export class InputDropdown extends React.Component<InputDropdownProps, null> {

    private renderOptions(): React.JSX.Element[] {
        let result: React.JSX.Element[] = []
        this.props.values.forEach((item, index) => {
            result.push(<option key={index} value={item.code}>{item.value}</option>)
        })

        return result
    }

    render() {
        return (
            <div className={"form-group"}>
                <label className={"form-input_name control-label"}>{this.props.fieldName}</label>
                <select className={"form-input_input form-control"}
                        onChange={(event) => this.props.onChange(event.target.value)} value={this.props.selectedValue}>
                    {
                        this.renderOptions()
                    }
                </select>
            </div>
        )
    }
}
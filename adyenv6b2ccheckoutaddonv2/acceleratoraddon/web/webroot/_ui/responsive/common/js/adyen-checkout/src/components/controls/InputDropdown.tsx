import React from "react";
import {CodeValueItem} from "../../reducers/types";
import {isNotEmpty} from "../../util/stringUtil";

interface InputDropdownProps {
    testId?: string
    values: CodeValueItem[]
    fieldName?: string
    selectedValue?: string
    placeholderText?: string
    placeholderDisabled?: boolean
    onChange?: (value: string) => void
}

export class InputDropdown extends React.Component<InputDropdownProps, null> {

    private getSelectedValue(): string {
        return this.props.selectedValue ? this.props.selectedValue : ""
    }

    private renderInput(): React.JSX.Element {
        if (isNotEmpty(this.props.testId)) {
            return <select id={this.props.testId}
                           className={"form-input_input form-control"}
                           onChange={(event) => this.props.onChange(event.target.value)}
                           value={this.getSelectedValue()}>
                {
                    this.renderOptions()
                }
            </select>
        }
        return <select className={"form-input_input form-control"}
                       onChange={(event) => this.props.onChange(event.target.value)} value={this.getSelectedValue()}>
            {
                this.renderOptions()
            }
        </select>

    }

    private renderOptions(): React.JSX.Element[] {
        let result: React.JSX.Element[] = []

        if (isNotEmpty(this.props.placeholderText)) {
            result.push(<option key={'placeholder'} value={""}
                                disabled={this.props.placeholderDisabled}>{this.props.placeholderText}</option>)
        }

        this.props.values.forEach((item, index) => {
            result.push(<option key={index} value={item.code}>{item.value}</option>)
        })

        return result
    }

    private renderLabel(): React.JSX.Element {
        if (isNotEmpty(this.props.fieldName)) {
            return <label className={"form-input_name control-label"}>{this.props.fieldName}</label>
        } else {
            return <></>
        }

    }

    render() {
        return (
            <div className={"form-group"}>
                {this.renderLabel()}
                {this.renderInput()}
            </div>
        )
    }
}
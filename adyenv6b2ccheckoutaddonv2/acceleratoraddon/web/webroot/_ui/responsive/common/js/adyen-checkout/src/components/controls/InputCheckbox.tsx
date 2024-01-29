import React from "react";


interface InputCheckboxProps {
    fieldName: string
    checked?: boolean
    onChange?: (value: boolean) => void
}

export class InputCheckbox extends React.Component<InputCheckboxProps, null> {

    render() {
        return (
            <div className={"form-group"}>
                <div className={"checkbox"}>
                    <label className={"form-group_checkbox_label"}>
                        <input className={"form-group_checkbox_label_input"} type={"checkbox"}
                               onChange={(event) => this.props.onChange ? this.props.onChange(event.target.checked) : ""}
                               checked={this.props.checked}/>
                        {this.props.fieldName}
                    </label>
                </div>
            </div>
        )
    }
}
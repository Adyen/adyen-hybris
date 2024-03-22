import React, {RefObject} from "react";


export class ScrollHere extends React.Component<{}, null> {
    private readonly ref: RefObject<any> = undefined;

    constructor(props: {}) {
        super(props);
        this.ref = React.createRef();

    }

    componentDidMount() {
        if (this.ref) {
            this.ref.current.scrollIntoView({behavior: 'smooth'})
        }
    }

    render() {
        // return <React.Fragment ref={this.ref}></React.Fragment>
        return <div ref={this.ref}></div>
    }
}
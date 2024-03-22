import React from 'react'
import './App.scss';
import CartDetails from "./components/cart-details/CartDetails";
import {TranslationService} from "./service/translationService";
import {translationsStore} from "./store/translationsStore";

interface State {
    translationsInitialized: boolean
}

class App extends React.Component<React.PropsWithChildren, State> {

    constructor(props: any) {
        super(props);
        this.state = {
            translationsInitialized: false
        }
    }

    async componentDidMount() {
        let success = await TranslationService.fetchTranslations();
        if (success) {
            this.setState({translationsInitialized: true})
        }
    }

    render() {
        if (!this.state.translationsInitialized) {
            return <></>
        }

        return (
            <>
                <div className="col-sm-6">
                    <div className="checkout-headline">
                        <span className="glyphicon glyphicon-lock"></span>
                        {translationsStore.get("checkout.multi.secure.checkout")}
                    </div>
                    <div className={"checkout-steps"}>
                        {this.props.children}
                    </div>
                </div>

                <div className="col-sm-6 hidden-xs">
                    <CartDetails/>
                </div>
            </>
        );
    }
}

export default App;

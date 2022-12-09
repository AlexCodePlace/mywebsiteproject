import React, {Component} from 'react'
import './App.css'

import axios from 'axios'

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            responseData:'',
            date: '',
            no2: '',
            pm10: '',
            pm2_5: '',
            city: '',
            state: '',
            country: '',
        };

        this.handleClick = this.handleClick.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleClick(event) {
        axios.get('/pollution/current/city?' +
            'city=' + this.state.city +
            '&state='+ this.state.state +
            '&country=' + this.state.country)
            .then(response => this.setState({responseData: response.data}))
            .catch((err) => console.error(err));
        event.preventDefault();
    }

    render() {
        return (
            <form onSubmit={this.handleClick}>
                <label>City:
                    <input name="city" type="text" value={this.state.city} onChange={this.handleInputChange}/>
                </label>
                <br/>
                <label>State:
                    <input name="state" type="text" value={this.state.state} onChange={this.handleInputChange}/>
                </label>
                <br/>
                <label>Country:
                    <input name="country" type="text" value={this.state.country} onChange={this.handleInputChange} />
                </label>
                <br/>
                <input type="submit" value="Submit"/>
                <br/>
                <p> Date: {this.state.responseData.date}</p>
                <p> no2: {this.state.responseData.no2}</p>
                <p> pm10: {this.state.responseData.pm10}</p>
                <p> pm2_5: {this.state.responseData.pm2_5}</p>
            </form>
        );
    }
}
export default App
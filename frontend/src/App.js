import React, {Component} from 'react'
import './App.css'

import axios from 'axios'

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            responseData: '',
            date: '',
            aqi: '',
            no2: '',
            pm10: '',
            o3: '',
            pm2_5: '',
            city: '',
            state: '',
            country: '',
            resultCity: '',
            resultState: '',
            resultCountry: ''
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
            '&state=' + this.state.state +
            '&country=' + this.state.country)
            .then(response => this.setState({responseData: response.data}))
            .catch((err) => console.error(err));
        event.preventDefault();
    }

    render() {
        return (
            <form className="submitButtonContainer" onSubmit={this.handleClick}>
                <label>City:
                    <input name="city" type="text" value={this.state.city} onChange={this.handleInputChange}/>
                </label>
                <br/>
                <label>State:
                    <input name="state" type="text" value={this.state.state} onChange={this.handleInputChange}/>
                </label>
                <br/>
                <label>Country:
                    <input name="country" type="text" value={this.state.country} onChange={this.handleInputChange}/>
                </label>
                <br/>
                <input className="submitButton" type="submit" value="Show current pollution info"/>
                <br/>
                <p><b>City:</b> {this.state.responseData.resultCity}
                    <b> State/Region:</b> {this.state.responseData.resultState}
                    <b> Country Code:</b> {this.state.responseData.resultCountry}</p>
                <p><b>Date/Time:</b> {this.state.responseData.date}</p>
                <p><b>Air Quality Index:</b> {this.state.responseData.aqi}</p>
                <p><b>NO<sub>2</sub>:</b> {this.state.responseData.no2}
                    <b> PM<sub>10</sub>:</b> {this.state.responseData.pm10}
                    <b> O<sub>3</sub>:</b> {this.state.responseData.o3}
                    <b> PM<sub>2.5</sub>:</b> {this.state.responseData.pm2_5}</p>
            </form>
        );
    }
}

export default App
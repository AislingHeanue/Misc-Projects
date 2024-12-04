use std::{
    fmt::{Display, Formatter},
    ops::{Add, Mul, Rem},
    result::Result,
};

use boolean::NodeBoolean;
use colour::NodeColour;
use numeric::NodeNumeric;

pub mod boolean;
pub mod colour;
pub mod numeric;

pub trait Evaluate {
    type Output;
    fn eval(&self, x: &f32, y: &f32) -> Result<Self::Output, &'static str>;
}

pub trait NodeConstructor {
    fn number(num: f32) -> NodeNumeric;
    fn conditional(cond: NodeBoolean, then: NodeNumeric, other: NodeNumeric) -> NodeNumeric;
    fn greater_than(first: NodeNumeric, second: NodeNumeric) -> NodeBoolean;
    fn equal(first: NodeNumeric, second: NodeNumeric) -> NodeBoolean;
    fn x() -> NodeNumeric;
    fn y() -> NodeNumeric;
    fn colour(r: NodeNumeric, g: NodeNumeric, b: NodeNumeric) -> NodeColour;
    fn conditional_colour(cond: NodeBoolean, then: NodeColour, other: NodeColour) -> NodeColour;
}

pub struct Node {}

impl NodeConstructor for Node {
    fn colour(r: NodeNumeric, g: NodeNumeric, b: NodeNumeric) -> NodeColour {
        NodeColour::RGB(Box::new(r), Box::new(g), Box::new(b))
    }

    fn number(num: f32) -> NodeNumeric {
        NodeNumeric::Number(num)
    }

    fn conditional(cond: NodeBoolean, then: NodeNumeric, other: NodeNumeric) -> NodeNumeric {
        NodeNumeric::If(Box::new(cond), Box::new(then), Box::new(other))
    }
    fn conditional_colour(cond: NodeBoolean, then: NodeColour, other: NodeColour) -> NodeColour {
        NodeColour::If(Box::new(cond), Box::new(then), Box::new(other))
    }

    fn greater_than(first: NodeNumeric, second: NodeNumeric) -> NodeBoolean {
        NodeBoolean::GreaterThan(Box::new(first), Box::new(second))
    }
    fn equal(first: NodeNumeric, second: NodeNumeric) -> NodeBoolean {
        NodeBoolean::Equal(Box::new(first), Box::new(second))
    }

    fn x() -> NodeNumeric {
        NodeNumeric::X
    }

    fn y() -> NodeNumeric {
        NodeNumeric::Y
    }
}

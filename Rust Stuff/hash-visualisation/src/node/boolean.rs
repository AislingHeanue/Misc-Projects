use super::Evaluate;
use super::NodeNumeric;
use std::fmt::Display;
use std::fmt::Formatter;

pub enum NodeBoolean {
    GreaterThan(Box<NodeNumeric>, Box<NodeNumeric>),
    Equal(Box<NodeNumeric>, Box<NodeNumeric>),
}

impl Display for NodeBoolean {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            NodeBoolean::GreaterThan(first, second) => write!(f, "{} > {}", first, second),
            NodeBoolean::Equal(first, second) => write!(f, "{} == {}", first, second),
        }
    }
}

impl Evaluate for NodeBoolean {
    type Output = bool;
    fn eval(&self, x: &f32, y: &f32) -> Result<bool, &'static str> {
        match self {
            NodeBoolean::GreaterThan(first, second) => Ok(first.eval(x, y)? > second.eval(x, y)?),
            NodeBoolean::Equal(first, second) => Ok(first.eval(x, y)? == second.eval(x, y)?),
        }
    }
}
